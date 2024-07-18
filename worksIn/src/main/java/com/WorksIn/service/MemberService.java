package com.WorksIn.service;

import com.WorksIn.dto.MemberResponseDto;
import com.WorksIn.dto.MemberUFDto;
import com.WorksIn.entity.Member;
import com.WorksIn.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor // final, @Nonnull 변수에 붙으면 자동주입(Autowired)
public class MemberService implements UserDetailsService,MemberImpl {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    public Member saveMember(Member member){
        validateDuplicateMember(member);
        return memberRepository.save(member);
    }
    private void validateDuplicateMember(Member member){
        Member findMember = memberRepository.findByEmail(member.getEmail());
        if(findMember != null){
            throw new IllegalStateException("이미 가입된 회원입니다.");
        }
        findMember = memberRepository.findByTel(member.getTel());
        if(findMember != null){
            throw new IllegalStateException("이미 가입된 전화번호입니다.");
        }
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Member member = memberRepository.findByEmail(email);

        if(member == null){
            throw new UsernameNotFoundException(email);
        }
        return User.builder().username(member.getEmail())
                .password(member.getPassword())
                .roles(member.getRole().toString())
                .build();
    }

    @Override
    public MemberResponseDto findMember(String email) throws UsernameNotFoundException{
        Member member = memberRepository.findByEmail(email);

        if(member == null){
            throw new UsernameNotFoundException(email);
        }

        MemberResponseDto result = MemberResponseDto.builder()
                .member(member)
                .build();

        return result;
    }

    @Override
    public Long updateMember(MemberUFDto memberUFDto) throws UsernameNotFoundException{
        Member member = memberRepository.findByEmail(memberUFDto.getEmail());

        if(member == null){
            throw new UsernameNotFoundException("이메일이 존재하지 않습니다.");
        }

        memberUFDto.setPassword(passwordEncoder.encode(memberUFDto.getPassword()));
        member.updateMember(memberUFDto.getPassword(), memberUFDto.getPostcode(), memberUFDto.getAddress(),
                memberUFDto.getDetailAddress(), memberUFDto.getExtraAddress());
        memberRepository.save(member);

        return member.getId();
    }

    @Override
    public boolean withdrawal(String email, String password) throws UsernameNotFoundException {
        Member member = memberRepository.findByEmail(email);

        if(member == null){
            throw new UsernameNotFoundException(email);
        }

        if(passwordEncoder.matches(password, member.getPassword())){
            memberRepository.delete(member);
            return true;
        }else{
            return false;
        }
    }
}
