package com.WorksIn.service;

import com.WorksIn.dto.MemberResponseDto;
import com.WorksIn.dto.MemberUFDto;

public interface MemberImpl {

    MemberResponseDto findMember(String email);

    Long updateMember(MemberUFDto memberUFDto);

    boolean withdrawal(String email, String password);
}
