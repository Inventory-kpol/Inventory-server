package kpol.Inventory.global.security.oauth2.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import kpol.Inventory.global.exception.CustomException;
import kpol.Inventory.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class NaverOAuth2UserUnlink implements OAuth2UserUnlink {

    private static final String URL = "https://nid.naver.com/oauth2.0/token";
    private final RestTemplate restTemplate;

    @Value("${spring.security.oauth2.client.registration.naver.client-id}")
    String clientId;

    @Value("${spring.security.oauth2.client.registration.naver.client-secret")
    String clientSecret;

    @Override
    public void unlink(String accessToken) {

        String url = URL +
                "?service_provider=NAVER" +
                "&grant_type=delete" +
                "&client_id=" +
                clientId +
                "&client_secret=" +
                clientSecret +
                "&access_token=" +
                accessToken;

        UnlinkResponse unlinkResponse = restTemplate.getForObject(url, UnlinkResponse.class);

        if (unlinkResponse != null && !"success".equalsIgnoreCase(unlinkResponse.getResult())) {
            throw new CustomException(ErrorCode.OAUTH_NAVER_UNLINK_FAILED);
        }
    }

    @Getter
    @RequiredArgsConstructor
    public static class UnlinkResponse {

        @JsonProperty
        private final String accessToken;

        private final String result;
    }
}
