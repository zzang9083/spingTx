package hello.springtx.apply;


import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@SpringBootTest
public class TxLevelTest {

    @Slf4j
    @Transactional(readOnly = true)
    static class LevelService {

        @Autowired LevelService levelService;

        @Test
        void orderTest() {
            levelService.write(); // tx active = true , tx readonly = false : 트랜잭션 레벨 - method
            levelService.read();  // tx active = true , tx readonly = true  : 트랜잭션 레벨 - class

        }


        @TestConfiguration
        static class TxLevelTestConfig {
            @Bean
            LevelService levelService() {
                return new LevelService();
            }
        }

        @Transactional(readOnly = false)
        public void write() {
            log.info("call write");
            printTxInfo();
        }

        public void read() {
            log.info("call read");
            printTxInfo();
        }

        public void printTxInfo() {
            boolean txActive = TransactionSynchronizationManager.isActualTransactionActive();
            log.info("tx active={}", txActive);
            boolean readOnly = TransactionSynchronizationManager.isCurrentTransactionReadOnly();
            log.info("tx readonly={}", readOnly);


        }
    }
}
