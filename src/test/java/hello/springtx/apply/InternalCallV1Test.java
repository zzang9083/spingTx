package hello.springtx.apply;

import lombok.extern.slf4j.Slf4j;
import org.h2.command.dml.Call;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public class InternalCallV1Test {

    @Slf4j
    static class CallService {

        @Autowired CallService callService;


        /**
         * (sample 1)
         * 외부에서 (@Transactional이 붙은)internal 함수가 호출하는 일반적인 케이스
         * -> 트랜잭션이 수행됨
         * */
        @Test
        void internalCall() {
            callService.internal();
        }

        /**
         * (Sample 2)
         * 1. 외부에서 (@Transactional이 안 붙은)external 함수가 호출
         * 2. external 함수에서 (@Transactional이 붙은)internal 함수가 호출
         * -> 트랜잭션이 붙은 internal 함수에서 트랜잭션이 수행이 안 된다..!
         * -> 왜 안붙었나? 트랜잭션 프록시의 트랜잭션 메소드를 호출하지 않고, 실제 객체(this)의 메소드를 호출한다.
         * (프록시 방식의 AOP 한계) @Transactional을 사용하는 트랜잭션 AOP는 프록시를 사용한다. 프록시를 사용하면 메소드 내부 호출에 프록시를 적용할 수 없다.
         * SOL : 내부 호출을 피하기 위해서 internal() 메소드를 별도의 클래소로 분리한다.
         * */
        @Test
        void externalCall() {
            callService.external();
        }

        public void external() {
            log.info("call external");
            printTxInfo();
            internal(); // (= this.internal)
        }

        @Transactional
        public void internal() {
            log.info("call internal");
            printTxInfo();
        }



        public void printTxInfo() { // 트랜잭션 실행 확인
            boolean txActive = TransactionSynchronizationManager.isActualTransactionActive();
            log.info("tx active={}", txActive);
        }

        @Test
        void printProxy() {
            log.info("callService class={}", callService.getClass());
        }

        @TestConfiguration
        static class InterCallV1TestConfig {
            @Bean
            CallService callService() {
                return new CallService();
            }

        }
    }
}
