package com.WorksIn.controller;

import com.WorksIn.dto.CartDetailDto;
import com.WorksIn.dto.CartItemDto;
import com.WorksIn.dto.CartOrderDto;
import com.WorksIn.entity.Member;
import com.WorksIn.repository.MemberRepository;
import com.WorksIn.service.CartService;
import com.WorksIn.service.OrderService;
import com.WorksIn.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;
    private final MemberRepository memberRepository;
    private final PaymentService paymentService;
    private final OrderService orderService;

//    @Autowired
//    public CartController(CartService cartService, PaymentService paymentService) {
//        this.cartService = cartService;
//        this.paymentService = paymentService;
//    }
    @PostMapping(value = "/cart")
    public @ResponseBody ResponseEntity order(@RequestBody @Valid CartItemDto cartItemDto,
                                              BindingResult bindingResult, Principal principal) {
        if (bindingResult.hasErrors()) {
            StringBuilder sb = new StringBuilder();
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                sb.append(fieldError.getDefaultMessage());
            }
            return new ResponseEntity<String>(sb.toString(), HttpStatus.BAD_REQUEST);
        }
        Long cartItemId;
        try {
            cartItemId = cartService.addCart(cartItemDto, emailChk(principal));
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<Long>(cartItemId, HttpStatus.OK);
    }

    @GetMapping(value = "/cart")
    public String orderHist(Principal principal, Model model) {
        List<CartDetailDto> cartDetailDtoList;

        cartDetailDtoList = cartService.getCartList(emailChk(principal));
        int price = 0;
        for (int i = 0; i < cartDetailDtoList.size(); i++) {
            price += cartDetailDtoList.get(i).getPrice() * cartDetailDtoList.get(i).getCount();
        }
        System.out.println(emailChk(principal));
        model.addAttribute("cartItems", cartDetailDtoList);
        model.addAttribute("priceHap", price);
        return "cart/cartList";
    }

    public String emailChk(Principal principal) {
        String email = principal.getName();
        if (!email.contains("@")) {
            email = cartService.EmailSend();
        }
        return email;
    }


    @GetMapping(value = "/check")
    public String nullcheck(Principal principal) {
        String email = principal.getName();
        String email2 = cartService.EmailSend();
        System.out.println(email2);
        //System.out.println(((SessionUser)httpSession.getAttribute("user")).getEmail());
//        System.out.println(httpSession.getAttribute("member"));
        System.out.println("이메일 체크 : " + email);

        return "check";
    }

    @PatchMapping(value = "/cartItem/{cartItemId}")
    public @ResponseBody ResponseEntity updateCartItem(@PathVariable("cartItemId") Long cartItemId, int count, Principal principal) {
        System.out.println(cartItemId);
        if (count <= 0) {
            return new ResponseEntity<String>("최소 1개이상 담아주세요.", HttpStatus.BAD_REQUEST);
        } else if (!cartService.validateCartItem(cartItemId, emailChk(principal))) {
            return new ResponseEntity<String>("수정권한이 없습니다.", HttpStatus.FORBIDDEN);
        }

        cartService.updateCartItemCount(cartItemId, count);
        return new ResponseEntity<Long>(cartItemId, HttpStatus.OK);
    }

    @DeleteMapping(value = "/cartItem/{cartItemId}")
    public @ResponseBody ResponseEntity deleteCartItem(@PathVariable("cartItemId") Long cartItemId, Principal principal) {
        if (!cartService.validateCartItem(cartItemId, emailChk(principal))) {
            return new ResponseEntity<String>("수정권한이 없습니다.", HttpStatus.FORBIDDEN);
        }
        cartService.deleteCartItem(cartItemId);
        return new ResponseEntity<Long>(cartItemId, HttpStatus.OK);
    }

    @PostMapping(value = "/cart/orders")
    public @ResponseBody ResponseEntity orderCartItem(@RequestBody CartOrderDto cartOrderDto, Principal principal) {
        System.out.println("dto 아이디 "+ cartOrderDto.getCartItemId());
        //CartorderDtoList List <- getCartOrderDtoList 통해서 views 내려운 리스트
        List<CartOrderDto> cartOrderDtoList = cartOrderDto.getCartOrderDtoList();
        System.out.println(cartOrderDtoList.get(0).getCartItemId());
        // null, size 0이면 실행
        if (cartOrderDtoList == null || cartOrderDtoList.size() == 0) {
            return new ResponseEntity("주문할 상품을 선택해주세요.", HttpStatus.FORBIDDEN);
        }
        // 전체 유효성 검사..
        for (CartOrderDto cartOrder : cartOrderDtoList) {
            if (!cartService.validateCartItem(cartOrder.getCartItemId(),emailChk(principal))) {
                return new ResponseEntity<String>("주문 권한이 없습니다.", HttpStatus.FORBIDDEN);
            }
        }
        String orderUid;
        try {
            // cart -> order
            // cartService -> orderService
            // cartOrderDtoList(CartOrderDtoList)

            orderUid = cartService.orderCartItem(cartOrderDtoList, emailChk(principal));
            System.out.println("안녕안녕 orderuid = " + orderUid);
        } catch (Exception e) {
            System.out.println("안녕안녕? 나는 카트컨트롤러 포스트매핑 오류야");
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<String>(orderUid, HttpStatus.OK);
    }


    public Member findUser(Principal principal) {
        Member member = memberRepository.findByEmail(emailChk(principal));
        return member;
    }
}
