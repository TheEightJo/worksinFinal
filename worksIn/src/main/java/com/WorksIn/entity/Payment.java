package com.WorksIn.entity;

import com.WorksIn.constant.PaymentStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@Getter
@NoArgsConstructor
@Entity
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int price;
    private PaymentStatus status; // enum
    private String paymentUid; // 결제 고유번호

    @Builder
    public Payment(int price, PaymentStatus status, String paymentUid) {
        this.price = price;
        this.status = status;
        this.paymentUid=paymentUid;
    }

    public void changePaymentBySuccess(PaymentStatus status, String paymentUid) {
        this.status = status;
        this.paymentUid = paymentUid;
    }


}
