package com.WorksIn.dto;

import com.WorksIn.constant.Role;
import com.WorksIn.entity.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberResponseDto {

    private String name;
    private String email;
    private String password;
    private String postcode;
    private String address;
    private String detailAddress;
    private String extraAddress;
    private String tel;
    private Role role;

    @Builder
    public MemberResponseDto(Member member){
        this.name = member.getName();
        this.email = member.getEmail();
        this.password = member.getPassword();
        this.postcode = member.getPostcode();
        this.address = member.getAddress();
        this.detailAddress = member.getDetailAddress();
        this.extraAddress = member.getExtraAddress();
        this.tel = member.getTel();
        this.role = member.getRole();
    }
}
