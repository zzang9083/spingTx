package hello.springtx.order;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepsitory extends JpaRepository<Order, Long> {
}
