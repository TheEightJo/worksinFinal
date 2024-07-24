package com.WorksIn.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class PaymentDto {


    private String itemNm;
    private String buyerName;
    private int count;
    private int orderPrice;
    private String buyerEmail;
    private String buyerAddress;
    private String orderUid;
    private String imgUrl;
    private String tel;


    @Builder
    public PaymentDto(String itemNm, String buyerName, int count,
                      int orderPrice, String buyerEmail, String buyerAddress, String orderUid, String imgUrl,
                      String tel) {
        this.itemNm = itemNm;
        this.buyerName = buyerName;
        this.count = count;
        this.orderPrice = orderPrice;
        this.buyerEmail = buyerEmail;
        this.buyerAddress = buyerAddress;
        this.orderUid = orderUid;
        this.imgUrl = imgUrl;
        this.tel = tel;
    }
}
