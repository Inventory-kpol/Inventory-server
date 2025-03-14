package kpol.Inventory.domain.member.service;

import jakarta.transaction.Transactional;
import kpol.Inventory.domain.member.dto.req.DeleteMemberRequestDto;
import kpol.Inventory.domain.member.dto.req.LoginRequestDto;
import kpol.Inventory.domain.member.dto.req.SignupRequestDto;
import kpol.Inventory.domain.member.dto.res.LoginResponseDto;
import kpol.Inventory.domain.member.dto.res.SignupResponseDto;
import kpol.Inventory.domain.member.entity.Member;
import kpol.Inventory.domain.member.repository.MemberRepository;
import kpol.Inventory.global.exception.CustomException;
import kpol.Inventory.global.exception.ErrorCode;
import kpol.Inventory.global.security.jwt.JwtTokenProvider;
import kpol.Inventory.global.security.jwt.JwtTokenRequestDto;
import kpol.Inventory.global.security.jwt.RefreshToken;
import kpol.Inventory.global.security.jwt.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public SignupResponseDto signup(SignupRequestDto signupRequestDto) {
        if (!signupRequestDto.getPassword().equals(signupRequestDto.getPasswordCheck())) {
            throw new CustomException(ErrorCode.PASSWORD_NOT_CORRECT);
        }

        Member member = new Member(signupRequestDto, passwordEncoder.encode(signupRequestDto.getPassword()));
        log.info("Member {} created", member);

        memberRepository.save(member);
        log.info("Member {} saved", member);

        return SignupResponseDto.builder()
                .email(member.getEmail())
                .nickname(member.getNickname())
                .build();
    }

    @Transactional
    public LoginResponseDto login(LoginRequestDto loginRequestDto) {

        Member member = memberRepository.findByEmail(loginRequestDto.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        boolean matchPassword = passwordEncoder.matches(loginRequestDto.getPassword(), member.getPassword());
        if (!matchPassword) {
            throw new CustomException(ErrorCode.PASSWORD_NOT_CORRECT);
        }
        log.info("Member {} Password Correct", member);

        UsernamePasswordAuthenticationToken authenticationToken = loginRequestDto.toAuthenticationToken();
        log.info("Member {} Authentication Token Created", member);

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        log.info("Member {} Authentication Token Authenticated", member);

        if (!authentication.isAuthenticated()) {
            throw new CustomException(ErrorCode.LOGIN_FAILED);
        }

        if (refreshTokenRepository.findByEmail(member.getEmail()).isPresent()) {
            refreshTokenRepository.deleteByEmail(member.getEmail());
        }

        LoginResponseDto loginResponseDto = jwtTokenProvider.generateToken(authentication);
        log.info("Member {} Login JWT Token Generated", member);

        RefreshToken refreshToken = RefreshToken.builder()
                .email(authentication.getName())
                .refreshToken(loginResponseDto.getRefreshToken())
                .build();
        refreshTokenRepository.save(refreshToken);
        log.info("Member {} Refresh JWT Token Generated", member);

        return loginResponseDto;
    }

    @Transactional
    public LoginResponseDto refresh(JwtTokenRequestDto jwtTokenRequestDto) {

        if (!jwtTokenProvider.validateToken(jwtTokenRequestDto.getRefreshToken())) {
            throw new CustomException(ErrorCode.JWT_REFRESH_TOKEN_EXPIRED);
        }

        Authentication authentication = jwtTokenProvider.getAuthentication(jwtTokenRequestDto.getRefreshToken());
        log.info("Member {} Authentication information extraction", authentication);

        String email = authentication.getName();
        log.info("Searching for refresh token with email: {}", email);
        RefreshToken refreshToken = refreshTokenRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new CustomException(ErrorCode.JWT_NOT_FOUND));

        if (!refreshToken.getRefreshToken().equals(jwtTokenRequestDto.getRefreshToken())) {
            throw new CustomException(ErrorCode.JWT_NOT_MATCH);
        }

        LoginResponseDto loginResponseDto = jwtTokenProvider.generateToken(authentication);
        log.info("Member {} Login JWT Token Generated", authentication);

        RefreshToken newRefreshToken = refreshToken.updateValue(loginResponseDto.getRefreshToken());
        log.info("Member {} Refresh JWT Token Updated", authentication);

        refreshTokenRepository.save(newRefreshToken);

        return loginResponseDto;
    }

    @Transactional
    public Boolean checkEmailDuplicated(String email) {
        if (memberRepository.existsByEmail(email)) {
            throw new CustomException(ErrorCode.EMAIL_DUPLICATED);
        }
        return true;
    }

    @Transactional
    public Boolean checkNicknameDuplicated(String nickname) {
        if (memberRepository.existsByNickname(nickname)) {
            throw new CustomException(ErrorCode.NICKNAME_DUPLICATED);
        }
        return true;
    }

    @Transactional
    public Boolean deleteMember(Member member, DeleteMemberRequestDto deleteMemberRequestDto) {
        if (!memberRepository.existsByEmail(member.getEmail())) {
            throw new CustomException(ErrorCode.MEMBER_NOT_FOUND);
        }

        boolean matchPassword = passwordEncoder.matches(deleteMemberRequestDto.getPassword(), member.getPassword());
        if (!matchPassword) {
            throw new CustomException(ErrorCode.PASSWORD_NOT_CORRECT);
        }
        log.info("Password Correct !");

        memberRepository.delete(member);
        log.info("Member {} deleted", member);
        return true;
    }

    public String getMainNickname(Member member) {
        return member.getNickname();
    }
}
