package com.WorksIn.controller;

import com.WorksIn.dto.OrderHistDto;
import com.WorksIn.dto.PaymentCallbackRequest;
import com.WorksIn.dto.PaymentDto;
import com.WorksIn.service.OrderService;
import com.WorksIn.service.PaymentService;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
@Slf4j
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final OrderService orderService;


    @GetMapping("/cart/payment/{id}")
    public String paymentPage(@PathVariable(name = "id", required = false) String id,
                              Model model) {
        System.out.println(id);
        PaymentDto requestDto = paymentService.findPaymentDto(id);
        OrderHistDto orderHistDto = orderService.getPaymentOrderList(id);
        System.out.println("가격 ============================="+requestDto.getOrderPrice());
        System.out.println("이름======================="+requestDto.getBuyerName());
        System.out.println("아이템이름 ============= "+requestDto.getItemNm());
        System.out.println("주소 ============= "+requestDto.getBuyerAddress());
        System.out.println("이메일 ============= "+requestDto.getBuyerEmail());

        model.addAttribute("orderItems",orderHistDto.getOrderItemDtoList());
        model.addAttribute("requestDto", requestDto);
        model.addAttribute("name", requestDto.getBuyerName());
        return "cart/payment";
    }

    @ResponseBody
    @PostMapping("/cart/payment")
    public ResponseEntity<IamportResponse<Payment>> validationPayment(@RequestBody PaymentCallbackRequest request) {
        System.out.println("postmapping badnya?");
        IamportResponse<Payment> iamportResponse = paymentService.paymentByCallback(request);
        System.out.println("결제 포스트매핑 받냐??");
        log.info("결제 응답={}", iamportResponse.getResponse().toString());

        return new ResponseEntity<>(iamportResponse, HttpStatus.OK);
    }
    @GetMapping("/success-payment")
    public String successPaymentPage(@RequestParam("merchant_uid") String merchantUid,
                                     @RequestParam("name") String name,
                                     @RequestParam("amount") String amount,
                                     @RequestParam("buyer_email") String buyerEmail,
                                     @RequestParam("buyer_name") String buyerName,
                                     @RequestParam("buyer_tel") String buyerTel,
                                     @RequestParam("buyer_addr") String buyerAddr,
                                     @RequestParam("delivery_request") String delivery,
                                     @RequestParam("shipping_name") String shippingName,
                                     @RequestParam("recipient_name") String recipientName,
                                     @RequestParam("recipient_phone") String recipientPhone,
                                     Model model) {
        OrderHistDto orderHistDto = orderService.getPaymentOrderList(merchantUid);
        System.out.println(orderHistDto.getOrderItemDtoList().size());
        model.addAttribute("orderItemList",orderHistDto.getOrderItemDtoList());
        model.addAttribute("orderDate",orderHistDto.getOrderDate());
        model.addAttribute("merchant_uid", merchantUid);
        model.addAttribute("name", name);
        model.addAttribute("amount", amount);
        model.addAttribute("buyer_email", buyerEmail);
        model.addAttribute("buyer_name", buyerName);
        model.addAttribute("buyer_tel", buyerTel);
        model.addAttribute("buyer_addr", buyerAddr);
        model.addAttribute("delivery",delivery);
        model.addAttribute("shippingName", shippingName);
        model.addAttribute("recipientName", recipientName);
        model.addAttribute("recipientPhone", recipientPhone);

        System.out.println(shippingName);
        System.out.println(buyerTel);
        System.out.println(recipientName);
        System.out.println(recipientPhone);

        return "/payment/success-payment";
    }

    @GetMapping("/fail-payment")
    public String failPaymentPage() {
        return "fail-payment";
    }
}
