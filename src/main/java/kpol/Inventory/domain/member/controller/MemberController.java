package kpol.Inventory.domain.member.controller;

import jakarta.validation.Valid;
import kpol.Inventory.domain.member.dto.req.*;
import kpol.Inventory.domain.member.dto.res.EmailResponseDto;
import kpol.Inventory.domain.member.dto.res.LoginResponseDto;
import kpol.Inventory.domain.member.dto.res.SignupResponseDto;
import kpol.Inventory.domain.member.service.EmailService;
import kpol.Inventory.domain.member.service.MemberService;
import kpol.Inventory.global.security.jwt.JwtTokenRequestDto;
import kpol.Inventory.global.security.jwt.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberController {

    private final MemberService memberService;
    private final EmailService emailService;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<SignupResponseDto> signup(@Valid @RequestBody SignupRequestDto signupRequestDto) {
        return ResponseEntity.status(HttpStatus.OK).body(memberService.signup(signupRequestDto));
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto loginRequestDto) {
        return ResponseEntity.status(HttpStatus.OK).body(memberService.login(loginRequestDto));
    }

    // 토큰 재발급
    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDto> refresh(@Valid @RequestBody JwtTokenRequestDto jwtTokenRequestDto) {
        return ResponseEntity.status(HttpStatus.OK).body(memberService.refresh(jwtTokenRequestDto));
    }

    // 이메일 중복 확인
    @GetMapping("/email/{email}")
    public ResponseEntity<Boolean> checkEmailDuplicated(@PathVariable String email) {
        return ResponseEntity.status(HttpStatus.OK).body(memberService.checkEmailDuplicated(email));
    }

    // 닉네임 중복 확인
    @GetMapping("/nickname/{nickname}")
    public ResponseEntity<Boolean> checkNicknameDuplicated(@PathVariable String nickname) {
        return ResponseEntity.status(HttpStatus.OK).body(memberService.checkNicknameDuplicated(nickname));
    }

    // 회원탈퇴
    @DeleteMapping
    public ResponseEntity<Boolean> deleteMember(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody DeleteMemberRequestDto deleteMemberRequestDto) {
        return ResponseEntity.status(HttpStatus.OK).body(memberService.deleteMember(userDetails.getMember(),deleteMemberRequestDto));
    }

    // 인증 이메일 전송
    @PostMapping("/sendEmail")
    public ResponseEntity<EmailResponseDto> sendEmail(@RequestBody @Valid EmailRequestDto emailRequestDto) {
        return ResponseEntity.status(HttpStatus.OK).body(emailService.joinEmail(emailRequestDto.getEmail()));
    }

    // 인증 이메일 인증
    @PostMapping("/mailAuthCheck")
    public ResponseEntity<EmailResponseDto> mailAuthCheck(@RequestBody  @Valid EmailCheckDto emailCheckDto) {
        return ResponseEntity.status(HttpStatus.OK).body(emailService.mailAuthCheck(emailCheckDto.getEmail(), emailCheckDto.getAuthNum()));
    }
}