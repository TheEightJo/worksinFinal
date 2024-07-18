package com.WorksIn.service;

import com.WorksIn.constant.PaymentStatus;
import com.WorksIn.dto.PaymentCallbackRequest;
import com.WorksIn.dto.PaymentDto;
import com.WorksIn.entity.Order;
import com.WorksIn.repository.OrderRepository;
import com.WorksIn.repository.PaymentRepository;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.request.CancelData;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;


@Service
@Transactional
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final IamportClient iamportClient;




    @Override
    public PaymentDto findPaymentDto(String orderUid) {
        System.out.println("오더uid = "+orderUid);
        Order order = orderRepository.findOrderAndPaymentAndMemberAndItems(orderUid)
                .orElseThrow(()-> new IllegalArgumentException("주문이 없습니다."));
        return PaymentDto.builder()
                .buyerName(order.getMember().getName())
                .buyerEmail(order.getMember().getEmail())
                .buyerAddress(order.getMember().getAddress())
                .count(order.getCount())
                .orderPrice(order.getTotalPrice())
                .itemNm(order.getItemNm())
                .orderUid(order.getOrderUid())
                .imgUrl(order.getImgUrl())
                .build();
    }

    @Override
    public IamportResponse<Payment> paymentByCallback(PaymentCallbackRequest request) {
        try{
            // 결제 단건 조회(아임포트)
            IamportResponse<Payment> iamportResponse = iamportClient.paymentByImpUid(request.getPaymentUid());
            System.out.println("결제조회~~~~ 결제번호 "+request.getPaymentUid());
            //주문내역 조회
            Order order = orderRepository.findOrderAndPayment(request.getOrderUid())
                    .orElseThrow(() -> new IllegalArgumentException("주문내역이 없습니다."));
            //결제완료가 아니면
            System.out.println("구매번호"+order.getOrderUid());
            System.out.println("구매이름"+order.getItemNm());
            if (!iamportResponse.getResponse().getStatus().equals("paid")) {
                // 주문 결제 삭제
                orderRepository.delete(order);
                paymentRepository.delete(order.getPayment());

                throw new RuntimeException("결제 미완료");
            }

            //DB에 저장된 금액
            System.out.println("페이먼트서비스 임플 int price 위엔데 여까지 오나??");
            int price = order.getPayment().getPrice(); // 여기 고쳐봐야함
            System.out.println("페이먼트서비스 임플 int price인데 이게 문젠가?" + price);
            //실제 결제금액
            int iamportPrice = iamportResponse.getResponse().getAmount().intValue();
            System.out.println("int iamportPrice 인데 여까지 오나?" + iamportPrice);
            //결제 금액 검증
            if (iamportPrice != price) {
                //주문, 결제 삭제
                orderRepository.delete(order);
                paymentRepository.delete(order.getPayment());

                //결제금액 위변조로 의심되는 결제금액 취소(아임포트)
                iamportClient.cancelPaymentByImpUid(new CancelData(iamportResponse.getResponse().getImpUid(),
                        true, new BigDecimal(iamportPrice)));
                throw new RuntimeException("결제금액 위변조 의심");
            }
            //결제 상태 변경
            order.getPayment().changePaymentBySuccess(PaymentStatus.OK, iamportResponse.getResponse().getImpUid());
            System.out.println("얄루");
            return iamportResponse;
        } catch (IamportResponseException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
