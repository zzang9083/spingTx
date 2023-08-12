package hello.springtx.apply;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public class InternalCallV2Test {

    @Slf4j
    @RequiredArgsConstructor
    static class CallService {

        @Autowired CallService callService;
        private final InternalService internalService;

        public CallService(InternalService internalService) {
            this.internalService = internalService;
        }

        @Test
        void externalCallV2() {
            callService.external();
        }

        public void external() {
            log.info("call external");
            printTxInfo();
            internalService.internal();
        }

        public void printTxInfo() { // 트랜잭션 실행 확인
            boolean txActive = TransactionSynchronizationManager.isActualTransactionActive();
            log.info("tx active={}", txActive);
        }


        @TestConfiguration
        static class InterCallV1TestConfig {
            @Bean
            CallService callService() {
                return new CallService(internalService());
            }

            @Bean
            InternalService internalService() {
                return new InternalService();
            }


        }

        /**
         * 트랜잭션 AOP 한계의 대응하기위해 메소드를 별도의 내부 클래스를 두어 사용한다.
         * */
        static class InternalService {

            @Transactional
            public void internal() {
                log.info("call internal");
                printTxInfo();
            }

            public void printTxInfo() { // 트랜잭션 실행 확인
                boolean txActive = TransactionSynchronizationManager.isActualTransactionActive();
                log.info("tx active={}", txActive);
            }
        }
    }
}
