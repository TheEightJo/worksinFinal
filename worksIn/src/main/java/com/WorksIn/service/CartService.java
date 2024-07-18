package com.WorksIn.service;

import com.WorksIn.dto.*;
import com.WorksIn.entity.Cart;
import com.WorksIn.entity.CartItem;
import com.WorksIn.entity.Item;
import com.WorksIn.entity.Member;
import com.WorksIn.repository.CartItemRepository;
import com.WorksIn.repository.CartRepository;
import com.WorksIn.repository.ItemRepository;
import com.WorksIn.repository.MemberRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {
    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderService orderService;
    private final HttpSession httpSession;

    public Long addCart(CartItemDto cartItemDto, String email) {
        Item item = itemRepository.findById(cartItemDto.getItemId()).orElseThrow(EntityNotFoundException::new);
        Member member = memberRepository.findByEmail(email);
        Cart cart = cartRepository.findByMemberId(member.getId());
        if (cart == null) {
            cart = Cart.createCart(member);
            cartRepository.save(cart);
        }

        CartItem savedCartItem = cartItemRepository.findByCartIdAndItemId(cart.getId(), item.getId());
        if (savedCartItem != null) {
            savedCartItem.addCount(cartItemDto.getCount());
            return savedCartItem.getId();
        } else {
            CartItem cartItem = CartItem.createCartItem(cart, item, cartItemDto.getCount());
            cartItemRepository.save(cartItem);
            return cartItem.getId();
        }
    }


    @Transactional(readOnly = true)
    public List<CartDetailDto> getCartList(String email) {
        List<CartDetailDto> cartDetailDtoList = new ArrayList<>();

        Member member = memberRepository.findByEmail(email);

        Cart cart = cartRepository.findByMemberId(member.getId());
        if (cart == null) {
            return cartDetailDtoList;
        }
        cartDetailDtoList = cartItemRepository.findCartDetailDtoList(cart.getId());
        return cartDetailDtoList;
    }



    @Transactional(readOnly = true)
    public boolean validateCartItem(Long cartItemId, String email) {
        // email을 이용해서 Member 엔티티 객체 추출
        Member curMember = memberRepository.findByEmail(email);
        // cartItemId를 이용해서 CartItem 엔티티 객체 추출
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(EntityExistsException::new);
        // Cart -> Member 엔티티 객체 추출
        Member savedMember = cartItem.getCart().getMember();
        // 현재 로그인된 Member == CartItem에 있는 Member -> 같지 않으면 return false
        if (!StringUtils.equals(curMember.getEmail(), savedMember.getEmail())) {
            return false;
        }
        // 현재 로그인된 Member == CartItem에 있는 Member -> 같으면 true 리턴
        return true;
    }

    public void updateCartItemCount(Long cartItemId, int count) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(EntityExistsException::new);
        cartItem.updateCount(count);
    }

    public void deleteCartItem(Long cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(EntityExistsException::new);
        cartItemRepository.delete(cartItem);
    }

    public String orderCartItem(List<CartOrderDto> cartOrderDtoList, String email) {
        // 주문Dto List 객체 생성
        List<OrderDto> orderDtoList = new ArrayList<>();
        // 카트 주문 List에 있는 목록 -> 카트 아이템 객체로 추출
        // 주문 Dto에 CartItem 정보를 담고
        // 위에 선언된 주문dto list에 추가.
        System.out.println(cartOrderDtoList.get(0).getCartItemId()+"==============-----------");
        for (CartOrderDto cartOrderDto : cartOrderDtoList) {
            CartItem cartItem = cartItemRepository.findById(cartOrderDto.getCartItemId())
                    .orElseThrow(EntityExistsException::new);
            OrderDto orderDto = new OrderDto();
            orderDto.setItemId(cartItem.getItem().getId());
            orderDto.setCount(cartItem.getCount());
            orderDtoList.add(orderDto);
        }
        // 주문DTO리스트 현재 로그인된 이메일을 매개변수 넣고
        System.out.println(orderDtoList.getFirst().getItemId()+"========================");
        // 주문 서비스 실행 -> 주문
        String orderUid = orderService.orders(orderDtoList, email).getOrderUid();
        //Cart에 있던 Item 주문이 되니까 CartItem 모두 삭제
        for (CartOrderDto cartOrderDto : cartOrderDtoList) {
            CartItem cartItem = cartItemRepository.findById(cartOrderDto.getCartItemId())
                    .orElseThrow(EntityExistsException::new);
            cartItemRepository.delete(cartItem);
        }
        return orderUid;
    }

    public String EmailSend(){
        System.out.println(((SessionUser)httpSession.getAttribute("user")).getEmail());

        return ((SessionUser)httpSession.getAttribute("user")).getEmail();
    }
    public String NameSend(){
        return ((SessionUser) httpSession.getAttribute("user")).getName();
    }

}