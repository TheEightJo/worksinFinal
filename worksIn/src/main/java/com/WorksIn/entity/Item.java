package com.WorksIn.entity;

import com.WorksIn.constant.ItemCategoryStatus;
import com.WorksIn.constant.ItemSellStatus;
import com.WorksIn.dto.ItemFormDto;
import com.WorksIn.exception.OutOfStockException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "item")
@Getter
@Setter
@ToString
public class Item extends BaseEntity{
    @Id
    @Column(name = "item_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;    //상품코드

    @Column(nullable = false,length = 50)
    private String itemNm;  //상품명

    @Column(name = "price", nullable = false)
    private int price;  // 가격

    @Column(nullable = false)
    private int stockNumber;    //수량

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String itemDetail;  // 상품상세설명

    @Enumerated(EnumType.STRING)
    private ItemSellStatus itemSellStatus;  //상품판매 상태

    @Enumerated(EnumType.STRING)
    private ItemCategoryStatus itemCategoryStatus; // 상품 카테고리

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "member_item",
            joinColumns = @JoinColumn(name = "member_id"),
            inverseJoinColumns = @JoinColumn(name = "item_id")
    )
    // private List<Member> member;
    public void updateItem(ItemFormDto itemFormDto){
        this.itemNm = itemFormDto.getItemNm();
        this.price = itemFormDto.getPrice();
        this.stockNumber = itemFormDto.getStockNumber();
        this.itemDetail = itemFormDto.getItemDetail();
        this.itemSellStatus = itemFormDto.getItemSellStatus();
        this.itemCategoryStatus = itemFormDto.getItemCategoryStatus();
    }

    public void removeStock(int stockNumber){
        int restStock = this.stockNumber - stockNumber;
        if (restStock<0){
            throw new OutOfStockException("상품의 재고가 부족합니다.(현재 재고 수량: "+this.stockNumber+")");
        }
        this.stockNumber = restStock;
    }

    public void addStock(int stockNumber){ // 주문취소시 다시 추가할 때
        this.stockNumber += stockNumber;
    }
}
