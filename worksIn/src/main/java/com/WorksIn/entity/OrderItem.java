package com.WorksIn.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class OrderItem extends BaseEntity{
    @Id
    @GeneratedValue
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id") //외래키
    private Item item;

    private String itemNm;

    private String imgUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id") //외래키
    private Order order;

//    private String itemNm.;

    private int orderPrice;
    private int count;
//    private LocalDateTime regTime;
//    private LocalDateTime updateTime;

    public static OrderItem createOrderItem(Item item, int count) {
        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item);
        orderItem.setCount(count);
        orderItem.setOrderPrice(item.getPrice());
        orderItem.setItemNm(item.getItemNm());

        item.removeStock(count);
        return orderItem;
    }
    public int getTotalPrice() {
        return orderPrice*count;
    }

    public void cancel() {
        this.getItem().addStock(count);
    }
}
