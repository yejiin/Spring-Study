package hello.core;

import hello.core.member.MemberRepository;
import hello.core.member.MemoryMemberRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

/**
 * @ComponentScan
 * @Componet가 붙은 모든 클래스를 자동으로 스프링 빈으로 등록
 *
 * @Autowired
 * 스프링 컨테이너가 자동으로 해당 스프링 빈을 찾아서 주입
 */
@Configuration
@ComponentScan(
        //기존 예제코드(AppConfig.class, TestConfig.class)를 남기고 테스트하기 위해서 설정정보는 컴포넌트 스캔 대상에서 제외함
        excludeFilters = @Filter(type = FilterType.ANNOTATION, classes = Configuration.class)
)
public class AutoAppConfig {

    @Bean(name = "memoryMemberRepository")
    public MemberRepository memberRepository() {
        return new MemoryMemberRepository();
    }
}
