package hello.springtx.order;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Slf4j
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepsitory orderRepsitory;

    @Transactional
    public void order(Order order) throws NotEnoughMoneyException {
        log.info("order 호출");
        orderRepsitory.save(order);

        log.info("결제 프로세스 진입");
        if(order.getUserName().equals("예외")) {
            log.info("시스템 예외 발생");
            throw new RuntimeException("시스템 예외");              // 언체크에러
        } else if(order.getUserName().equals("잔고부족")) {
            log.info("잔고 부족 비즈니스 예외 발생");
            order.setPayStatus("대기");
            throw new NotEnoughMoneyException("잔고가 부족합니다."); // 체크 에러
        } else {
            // 정상 승인
            log.info("정상 승인");
            order.setPayStatus("완료");
        }
        log.info("결제 프로세스 승인");
   }
}
