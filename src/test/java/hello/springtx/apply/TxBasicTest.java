package hello.springtx.apply;


import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
public class TxBasicTest {

    @Autowired BasicService basicService;

    @Test
    void proxyCheck() { // 트랜잭션 프록시 확인
        log.info("aop class = {}", basicService.getClass());
        assertThat(AopUtils.isAopProxy(basicService)).isTrue();
    }

    @Test
    void txTest() { // 트랜잭션 적용 확인
        basicService.tx(); // 적용
        basicService.nonTx(); // 미적용
    }

    @TestConfiguration
    static class TxApplyBasicConfig {
        // 빈객체 생성
        @Bean
        BasicService basicService() {
            return new BasicService();
        }
    }

    @Slf4j
    static class BasicService {
        @Transactional
        public void tx() {
            log.info("call tx");
            boolean txActive = TransactionSynchronizationManager.isActualTransactionActive(); // 트랜잭션 적용여부 확인
            log.info("tx active={}", txActive);

        }

        public void nonTx() {
            log.info("call nontx");
            boolean nonTxActive = TransactionSynchronizationManager.isActualTransactionActive(); // 트랜잭션 적용여부 확인
            log.info("tx active={}", nonTxActive);
        }
    }
}
