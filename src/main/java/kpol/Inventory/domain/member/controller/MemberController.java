package kpol.Inventory.domain.member.controller;

import jakarta.validation.Valid;
import kpol.Inventory.domain.member.dto.req.DeleteMemberRequestDto;
import kpol.Inventory.domain.member.dto.req.LoginRequestDto;
import kpol.Inventory.domain.member.dto.req.SignupRequestDto;
import kpol.Inventory.domain.member.dto.res.LoginResponseDto;
import kpol.Inventory.domain.member.dto.res.SignupResponseDto;
import kpol.Inventory.domain.member.service.MemberService;
import kpol.Inventory.global.security.jwt.JwtTokenRequestDto;
import kpol.Inventory.global.security.jwt.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberController {

    private final MemberService memberService;

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
<<<<<<< HEAD

    // 페이지 별 닉네임 반환
    @GetMapping("/page/nickname")
    public ResponseEntity<String> getMainNickname(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.status(HttpStatus.OK).body(memberService.getMainNickname(userDetails.getMember()));
    }
=======
>>>>>>> 6874030f37a64461c1265af353bdf48cab2fee8c
}