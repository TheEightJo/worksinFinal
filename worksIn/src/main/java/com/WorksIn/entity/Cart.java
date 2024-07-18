package com.WorksIn.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name= "cart")
@Data
public class Cart {
    @Id
    @Column(name = "cart_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id") //JoinColumn 매핑할 외래키르 지정하고, 외래키 이름 설정
    //name을 명시하지않으면 JPA가 알아서 ID를 찾지만 원하는 이름이 아닐 수 있습니다.
    private Member member;

    public static Cart createCart(Member member) {
        Cart cart = new Cart();
        cart.setMember(member);
        return cart;
    }


}
