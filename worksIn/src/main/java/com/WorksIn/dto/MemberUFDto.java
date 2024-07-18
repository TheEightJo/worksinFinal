package com.WorksIn.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
public class MemberUFDto {
    private String name;
    private String email;
    @NotEmpty(message = "비밀번호를 입력해주세요.")
    @Length(min = 8, message = "안전을 위해 비밀번호는 8자 이상 입력해주세요.")
    private String password;
    private String postcode;
    private String address;
    private String extraAddress;
    @NotEmpty(message = "상세주소를 입력해주세요.")
    private String detailAddress;
    private String tel;
}
