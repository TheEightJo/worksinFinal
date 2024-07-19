package com.WorksIn.service;

import com.WorksIn.constant.PaymentStatus;
import com.WorksIn.dto.OrderDto;
import com.WorksIn.dto.OrderHistDto;
import com.WorksIn.dto.OrderItemDto;
import com.WorksIn.entity.*;
import com.WorksIn.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {
    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final OrderRepository orderRepository;
    private final ItemImgRepository itemImgRepository;
    private final PaymentRepository paymentRepository;

    public Order order(OrderDto orderDto, String email) {
        Item item = itemRepository.findById(orderDto.getItemId())
                .orElseThrow(EntityNotFoundException::new);
        Member member = memberRepository.findByEmail(email);

        List<OrderItem> orderItemList = new ArrayList<>();
        OrderItem orderItem = OrderItem.createOrderItem(item, orderDto.getCount());
        orderItemList.add(orderItem);

        Order order = Order.createOrder(member, orderItemList);
        orderRepository.save(order);

        order.setPayment(paymentOrder(order.getOrderUid()));
        // 주문서에 결제정보 넣어버리기

        return orderRepository.save(order);
    }


    @Transactional(readOnly = true)
    public Page<OrderHistDto> getOrderList(String email, Pageable pageable) {
        List<Order> orders = orderRepository.findOrders(email, pageable);
        Long totalCount = orderRepository.countOrder(email);
        List<OrderHistDto> orderHistDtos = new ArrayList<>();
        // Order -> OrderHistDto
        // OrderItem -> OrderItemDto

        for (Order order : orders) {
            OrderHistDto orderHistDto = new OrderHistDto(order);
            List<OrderItem> orderItems = order.getOrderItems();
            for (OrderItem orderItem : orderItems) {
                ItemImg itemImg = itemImgRepository.findByItemIdAndRepImgYn(orderItem.getItem().getId(), "Y");
                OrderItemDto orderItemDto = new OrderItemDto(orderItem, itemImg.getImgUrl());
                orderHistDto.addOrderItemDto(orderItemDto);
            }
            orderHistDtos.add(orderHistDto);
        }
        return new PageImpl<OrderHistDto>(orderHistDtos, pageable, totalCount);
    }

    @Transactional(readOnly = true)
    public OrderHistDto getPaymentOrderList(String orderUid) {
        Order order = orderRepository.findOrder(orderUid);

        OrderHistDto orderHistDto = new OrderHistDto(order);
        List<OrderItem> orderItems = order.getOrderItems();
        for (OrderItem orderItem : orderItems) {
            ItemImg itemImg = itemImgRepository.findByItemIdAndRepImgYn(orderItem.getItem().getId(), "Y");
            OrderItemDto orderItemDto = new OrderItemDto(orderItem, itemImg.getImgUrl());
            orderHistDto.addOrderItemDto(orderItemDto);
        }
        return orderHistDto;
    }

    @Transactional(readOnly = true)
    public boolean validateOrder(Long orderId, String email) {
        Member curMember = memberRepository.findByEmail(email);
        Order order = orderRepository.findById(orderId).orElseThrow(EntityNotFoundException::new);
        Member savedMember = order.getMember();

        if (!StringUtils.equals(curMember.getEmail(), savedMember.getEmail())) {
            return false;
        }
        return true;
    }

    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(EntityNotFoundException::new);
        order.cancelOrder();
    }

    public Order orders(List<OrderDto> orderDtoList, String email) {
        //Member 엔티티 객체 추출
        Member member = memberRepository.findByEmail(email);
        //주문 Item 리스트 객체 생성
        List<OrderItem> orderItemList = new ArrayList<>();
        // 주문 dto List에 있는 개겣만큼 반복
        for (OrderDto orderDto : orderDtoList) {
            // 주문 -> item 객체 추출
            Item item = itemRepository.findById(orderDto.getItemId()).orElseThrow(EntityNotFoundException::new);
            // 주문 Item 생성
            OrderItem orderItem = OrderItem.createOrderItem(item, orderDto.getCount());
            System.out.println("아이템이름 : " + orderItem.getItemNm());
            // 주문 Item List에 추가
            orderItemList.add(orderItem);
        }
        //////////////// 주문 Item List가 완성 ///////////
        Order order = Order.createOrder(member, orderItemList);
        orderRepository.save(order);

        order.setPayment(paymentOrder(order.getOrderUid()));
        // 주문서에 결제정보 넣어버리기

        return orderRepository.save(order);
    }

    public Payment paymentOrder(String orderUid) {

        Order orders = orderRepository.findOrder(orderUid);
        Payment payment = Payment.builder()
                .price(orders.getTotalPrice())
                .status(PaymentStatus.READY)
                .build();

        return paymentRepository.save(payment);
    }
}
