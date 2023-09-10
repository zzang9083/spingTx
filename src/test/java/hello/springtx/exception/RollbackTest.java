package hello.springtx.exception;


import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
public class RollbackTest {

    @Autowired RollbakService service;

    @Test
    void runtimeException() {
        service.runtimeException(); // 언체크 예외 발생 : 롤백
    }

    @Test
    void checkedException() throws RollbakService.MyException {
        service.checkedException(); // 체크 예외 발생: 커밋
    }

    @TestConfiguration
    static class RollbackTestConfig {

        @Bean
        RollbakService rollbakService() {
            return new RollbakService();
        }
    }

    @Slf4j
    static class RollbakService {

        // 언체크 예외 발생 : 롤백
        @Transactional
        public void runtimeException() {
            log.info("call runtimeException");
            throw new RuntimeException();
        }

        // 체크 예외 발생: 커밋
        @Transactional
        public void checkedException() throws MyException{
            log.info("call checkedException");
            throw new MyException();
        }

        //체크 예외 rollbackFor 지정: 롤백
        @Transactional(rollbackFor = MyException.class)
        public void rollbackFor() throws MyException{
            log.info("call rollbackFor");
            throw new MyException();
        }


        static class MyException extends Exception {

        }
    }
}
