package jpabook.jpashop.api;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController  //@Controller + @ResponseBody
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    /**
     * 회원 조회 API
     * 응답 값으로 엔티티를 직접 외부에 노출
     *  ++ 엔티티에 프레젠테이션 계층을 위한 로직 추가 (@JsonIgnore)
     *  ++ 별도의 DTO 를 파라미터로 받아야 한다.
     *  ++ 컬렉션을 직접 반환하면 API 스펙 변경 어려움 (별도의 Result 클래스 생성으로 해결)
     */
    @GetMapping("/api/v1/members")
    public List<Member> membersV1() {
        return memberService.findMembers();
    }

    /**
     * 회원 조회 API
     * 응답 값으로 엔티티가 아닌 별도의 DTO 사용
     */
    @GetMapping("/api/v2/members")
    public Result membersV2() {

        List<Member> findMembers = memberService.findMembers();
        //엔티티 -> DTO 변환
        List<MemberDto> collect = findMembers.stream()
                .map(m -> new MemberDto(m.getName()))
                .collect(Collectors.toList());
//        return new Result(collect.size(), collect);
        return new Result(collect);
    }

    /**
     * 회원 등록 API
     * 엔티티를 Request Body 에 직접 매핑
     *  ++ 엔티티에 프레젠테이션 계층을 위한 로직 추가 (@NotEmpty)
     *  ++ 별도의 DTO 를 파라미터로 받아야 한다.
     */
    @PostMapping("/api/v1/members")
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member) {
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    /**
     * 회원 등록 API
     * 엔티티 대신에 DTO 를 파라미터로 받음
     *  ++ 엔티티와 프레젠테이션 계층을 위한 로직을 분리할 수 있다.
     */
    @PostMapping("/api/v2/members")
    public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request) {

        Member member = new Member();
        member.setName(request.name);

        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    /**
     * 회원 수정 API
     * PUT : 전체 업데이트 할 때 사용
     * PATCH, POST : 부분 업데이트 할 때 사용
     */
    @PatchMapping("/api/v2/members/{id}")
    public UpdateMemberResponse updateMemberV2(
            @PathVariable("id") Long id,
            @RequestBody @Valid UpdateMemberRequest request) {
        memberService.update(id, request.getName());  //변경 감지를 사용해서 데이터 수정, update()에서 member 를 반환해도 되지만 영속상태가 끊긴 상태로 반횐됨
        Member findMember = memberService.findOne(id);
        return new UpdateMemberResponse(id, findMember.getName());
    }

    //클래스 내에서만 사용할 DTO 는 inner class 로 생성
    //entity 는 getter 정도만 사용하는 것을 권장.
    //dto 는 비즈니스 로직이 없기 때문에 @Data 사용
    @Data
    @AllArgsConstructor
    static class Result<T> {  //껍데기 DTO
//        private int count;
        private T data;
    }

    @Data
    @AllArgsConstructor
    static class MemberDto {
        private String name;
    }

    @Data
    static class CreateMemberRequest {
        private String name;
    }

    @Data
    static class CreateMemberResponse {
        private Long id;

        public CreateMemberResponse(Long id) {
            this.id = id;
        }
    }

    @Data
    static class UpdateMemberRequest {
        private String name;
    }

    @Data
    @AllArgsConstructor
    static class UpdateMemberResponse {
        private Long id;
        private String name;
    }
}
