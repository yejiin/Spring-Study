package hello.core.singleton;

import hello.core.AppConfig;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;

public class StatefulServiceTest {

    @Test
    void statefulServiceSingleton() {
        ApplicationContext ac = new AnnotationConfigApplicationContext(TestConfig.class);

        StatefulService statefulService1 = ac.getBean("statefulService", StatefulService.class);
        StatefulService statefulService2 = ac.getBean("statefulService", StatefulService.class);

        //TreadA: 사용자A 10000원 주문
        statefulService1.order("userA", 10000);
        //TreadB: 사용자B 20000원 주문
        statefulService2.order("userB", 20000);

        //TreadA: 사용자A 주문 금액 조회
        int price = statefulService1.getPrice();
        //TreadA: 사용자A는 10000원을 기대했지만, 기대와 다르게 2000원 출력
        System.out.println("price = " + price);

        Assertions.assertThat(statefulService1.getPrice()).isEqualTo(20000);
    }

    @Test
    void statelessServiceSingleton() {
        ApplicationContext ac = new AnnotationConfigApplicationContext(TestConfig.class);

        StatelessService statelessService1 = ac.getBean("statelessService", StatelessService.class);
        StatelessService statelessService2 = ac.getBean("statelessService", StatelessService.class);

        //TreadA: 사용자A 10000원 주문
        int priceA = statelessService1.order("userA", 10000);
        //TreadB: 사용자B 20000원 주문
        int priceB = statelessService2.order("userB", 20000);

        //TreadA: 사용자A 주문 금액 조회, 10000원 출력
        System.out.println("priceA = " + priceA);
        //TreadB: 사용자B 주문 금액 조회, 20000원 출력
        System.out.println("priceB = " + priceB);

        Assertions.assertThat(priceA).isEqualTo(10000);
    }

    static class TestConfig {

        @Bean
        public StatefulService statefulService() {
            return new StatefulService();
        }

        @Bean
        public StatelessService statelessService() {
            return new StatelessService();
        }
    }
}
