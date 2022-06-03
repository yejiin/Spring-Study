package hello.jdbc.repository;

import hello.jdbc.domain.Member;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

class MemberRepositoryV0Test {

    MemberRepositoryV0 repository = new MemberRepositoryV0();

    @Test
    void curd() {
        // save
        Member member = new Member("memberV0", 10000);
        try {
            repository.save(member);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}