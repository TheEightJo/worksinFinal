package com.WorksIn.service;

import com.WorksIn.dto.PaymentCallbackRequest;
import com.WorksIn.dto.PaymentDto;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;

public interface PaymentService {
    // 결제 요청 데이터 조회
    PaymentDto findPaymentDto(String orderUid);



    //결제(콜백)
    IamportResponse<Payment> paymentByCallback(PaymentCallbackRequest request);
}
