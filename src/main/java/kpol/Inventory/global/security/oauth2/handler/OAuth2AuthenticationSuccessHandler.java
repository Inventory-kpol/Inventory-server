package kpol.Inventory.global.security.oauth2.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kpol.Inventory.domain.member.dto.res.LoginResponseDto;
import kpol.Inventory.global.security.oauth2.HttpCookieOAuth2AuthorizationRequestRepository;
import kpol.Inventory.global.security.oauth2.service.CustomOAuth2UserService;
import kpol.Inventory.global.security.oauth2.service.OAuth2UserPrincipal;
import kpol.Inventory.global.security.oauth2.user.OAuth2UserUnlinkManager;
import kpol.Inventory.global.security.oauth2.util.CookieUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Optional;

import static kpol.Inventory.global.security.oauth2.HttpCookieOAuth2AuthorizationRequestRepository.MODE_PARAM;
import static kpol.Inventory.global.security.oauth2.HttpCookieOAuth2AuthorizationRequestRepository.REDIRECT_URI_PARAM;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;
    private final OAuth2UserUnlinkManager oAuth2UserUnlinkManager;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
        throws IOException, ServletException {

        String targetUrl = setTargetUrl(request, response, authentication);

        clearAuthenticationAttributes(request, response);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    protected String setTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        Optional<String> redirectUrl = CookieUtils.getCookie(request, REDIRECT_URI_PARAM)
                .map(Cookie::getValue);

        String targetUrl = redirectUrl.orElse(getDefaultTargetUrl());

        String mode = CookieUtils.getCookie(request, MODE_PARAM)
                .map(Cookie::getValue)
                .orElse("");

        OAuth2UserPrincipal userPrincipal = getOAuth2UserDetails(authentication);

        if (userPrincipal == null) {
            return UriComponentsBuilder.fromUriString(targetUrl)
                    .queryParam("error", "Login Failed with Authentication Error")
                    .build().toUriString();
        }

        if ("login".equalsIgnoreCase(mode)) {
            if (!customOAuth2UserService.checkUserPresent(userPrincipal.getName())) {
                return UriComponentsBuilder.fromUriString(targetUrl)
                        .path("/signup")
                        .queryParam("provider", userPrincipal.getUserInfo().getProvider())
                        .build().toUriString();
            } else {
                LoginResponseDto loginResponseDto = customOAuth2UserService.oAuth2Login(authentication);
                return UriComponentsBuilder.fromUriString(targetUrl)
                        .path("/main")
                        .queryParam("accessToken", loginResponseDto.getAccessToken())
                        .queryParam("refreshToken", loginResponseDto.getRefreshToken())
                        .build().toUriString();
            }
        } else if ("unlink".equalsIgnoreCase(mode)) {
            oAuth2UserUnlinkManager.unlink(userPrincipal.getUserInfo().getProvider(), userPrincipal.getUserInfo().getAccessToken());

            return UriComponentsBuilder.fromUriString(targetUrl)
                    .build().toUriString();
        }

        return UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("error", "Login Failed with unexpected Error")
                .build().toUriString();
    }

    private OAuth2UserPrincipal getOAuth2UserDetails(Authentication authentication) {
        Object principal = authentication.getPrincipal();

        if (principal instanceof OAuth2UserPrincipal) {
            return (OAuth2UserPrincipal) principal;
        }

        return null;
    }

    protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        httpCookieOAuth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }
}
