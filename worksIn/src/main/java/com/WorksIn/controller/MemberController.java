package com.WorksIn.controller;

import com.WorksIn.dto.MemberFormDto;
import com.WorksIn.dto.MemberResponseDto;
import com.WorksIn.dto.MemberUFDto;
import com.WorksIn.entity.Member;
import com.WorksIn.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RequestMapping("/members")
@Controller
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private final MessageService messageService;
    private final MemberImpl memberImpl;
    private final CartService cartService;
    String confirm = "";

    String telConfirm = "";
    boolean confirmCheck = false;
    boolean telConfirmCheck = false;


    @GetMapping(value = "/new")
    public String memberForm(Model model){
        model.addAttribute("memberFormDto",new MemberFormDto());
        return "member/memberForm";
    }
    @PostMapping(value = "/new")
    public String memberForm(@Valid MemberFormDto memberFormDto, BindingResult bindingResult,
                             Model model) {
        if(bindingResult.hasErrors()){
            return "member/memberForm";
        }
//        if(!confirmCheck){
//            model.addAttribute("errorMessage","이메일 인증이 필요합니다.");
//            return "member/memberForm";
//        }
//        if(!telConfirmCheck){
//            model.addAttribute("errorMessage", "문자 인증이 필요합니다.");
//            return "member/memberForm";
//        }
        try{
            Member member = Member.createMember(memberFormDto, passwordEncoder);
            memberService.saveMember(member);
        }
        catch (IllegalStateException e){
            model.addAttribute("errorMessage",e.getMessage());
            return "member/memberForm";
        }
        return "redirect:/";
    }

    @GetMapping(value = "/login")
    public String loginMember(){
        return "/member/memberLoginForm";
    }
    @GetMapping(value = "/login/error")
    public String loginError(Model model){
        model.addAttribute("loginErrorMsg","아이디 또는 비밀번호를 확인해주세요");
        return "/member/memberLoginForm";
    }
    @GetMapping(value = "/modify")
    public String memberUpdateForm(Model model,Principal principal){
        MemberResponseDto member = memberImpl.findMember(emailChk(principal));
        model.addAttribute("member", member);
        model.addAttribute("memberUFDto",new MemberUFDto());

        return "member/memberUpdateForm";
    }
    public String emailChk(Principal principal) {
        String email = principal.getName();
        if (!email.contains("@")) {
            email = cartService.EmailSend();
        }
        return email;
    }

    @PostMapping(value = "/modify")
    public String memberUpdateForm(@Valid MemberUFDto memberUFDto, BindingResult bindingResult, Model model){
        if(bindingResult.hasErrors()){
            return "member/memberUpdateForm";
        }
        try{
            memberService.updateMember(memberUFDto);
        }
        catch (IllegalStateException e){
            model.addAttribute("errorMessage",e.getMessage());
            return "member/memberUpdateForm";
        }

        return "redirect:/";
    }

    @GetMapping(value = "/withdrawal")
    public String memberWithdrawalForm(){
        return "/member/withdrawal";
    }

    @PostMapping("/withdrawal")
    public String memberWithdrawal(@RequestParam String password, Model model, Authentication authentication){
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        boolean result = memberService.withdrawal(userDetails.getUsername(), password);

        if(result){
            return "redirect:/members/logout";
        }
        else{
            model.addAttribute("wrongPassword", "비밀번호가 맞지 않습니다.");
            return "/member/withdrawal";
        }
    }


    @PostMapping("/{email}/emailConfirm")
    public @ResponseBody ResponseEntity emailConfirm(@PathVariable("email") String email)
            throws Exception{
        confirm = mailService.sendSimpleMessage(email);
        return new ResponseEntity<String>("인증 메일이 전송되었습니다.", HttpStatus.OK);
    }

    @PostMapping("/{code}/codeCheck")
    public @ResponseBody ResponseEntity codeConfirm(@PathVariable("code")String code)
            throws Exception{
        if(confirm.equals(code)){
            confirmCheck = true;
            return new ResponseEntity<String>("인증 성공하였습니다.",HttpStatus.OK);
        }
        return new ResponseEntity<String>("인증 코드를 올바르게 입력해주세요.",HttpStatus.BAD_REQUEST);
    }

//    @PostMapping("/{tel}/messageSend")
//    public @ResponseBody ResponseEntity phoneConfirm(@PathVariable("tel") String tel)
//            throws Exception{
//        telConfirm = messageService.numberCheck(tel);
//        return new ResponseEntity<String>("인증 코드를 보냈습니다.", HttpStatus.OK);
//    }
//
//    @PostMapping("/{telcode}/telcodeCheck")
//    public @ResponseBody ResponseEntity telcodeConfirm(@PathVariable("telcode") String telcode)
//            throws Exception{
//        if(telConfirm.equals(telcode)){
//            telConfirmCheck = true;
//            return new ResponseEntity<String>("문자 인증이 완료되었습니다.", HttpStatus.OK);
//        }
//        return new ResponseEntity<String>("문자로 전송된 코드를 올바르게 입력해주세요.", HttpStatus.BAD_REQUEST);
//    }
}
