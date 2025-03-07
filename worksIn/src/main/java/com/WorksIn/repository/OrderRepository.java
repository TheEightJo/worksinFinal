package com.WorksIn.repository;

import com.WorksIn.entity.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("select o from Order o where o.member.email = :email order by o.orderDate desc")
    List<Order> findOrders(@Param("email") String email, Pageable pageable);

    @Query("select o from Order o where o.orderUid = :orderUid")
    Order findOrder(@Param("orderUid") String orderUid);

    @Query("select count(o) from Order o where o.member.email = :email")
    Long countOrder(@Param("email") String email);


    @Query("select o from Order o" +
            " left join fetch o.payment p" +
            " left join fetch o.member m" +
            " where o.orderUid = :orderUid")
    Optional<Order> findOrderAndPaymentAndMember(String orderUid);

        @Query("select o from Order o" +
            " left join fetch o.payment p" +
            " left join fetch o.member m" +
            " left join fetch o.orderItems oi" +
            " left join fetch oi.item i" +
            " where o.orderUid = :orderUid")
    Optional<Order> findOrderAndPaymentAndMemberAndItems(String orderUid);




    @Query("select o from Order o" +
            " left join fetch o.payment p" +
            " where o.orderUid = :orderUid")
    Optional<Order> findOrderAndPayment(String orderUid);
}
