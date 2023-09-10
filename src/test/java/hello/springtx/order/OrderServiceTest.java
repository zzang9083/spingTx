package hello.springtx.order;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class OrderServiceTest {

    @Autowired OrderService orderService;
    @Autowired OrderRepsitory orderRepsitory;

    @Test
    void complete() throws NotEnoughMoneyException {
        //given
        Order order = new Order();
        order.setUserName("정상");

        //when
        orderService.order(order);

        //then
        Order findOrder = orderRepsitory.findById(order.getId()).get();
        assertThat(findOrder.getPayStatus()).isEqualTo("완료");

    }

    @Test
    void runtimeException() throws NotEnoughMoneyException {
        //given
        Order order = new Order();
        order.setUserName("예외");

        //when
        Assertions.assertThatThrownBy(() -> orderService.order(order))
                        .isInstanceOf(RuntimeException.class);

        //then
        Optional<Order> orderOptional = orderRepsitory.findById(order.getId());
        assertThat(orderOptional.isEmpty()).isTrue();

    }

    @Test
    void businessException() throws NotEnoughMoneyException {
        //given
        Order order = new Order();
        order.setUserName("잔고부족");

        //when
        try {
            orderService.order(order);
        } catch (NotEnoughMoneyException e) {
            log.info("고객에게 잔고 부족을 알리고 별도의 계좌로 입금하도록 안내");
        }

        //then
        Order findOrder = orderRepsitory.findById(order.getId()).get();
        assertThat(findOrder.getPayStatus()).isEqualTo("대기");

    }
}