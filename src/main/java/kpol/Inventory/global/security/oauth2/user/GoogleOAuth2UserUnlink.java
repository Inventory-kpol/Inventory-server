package kpol.Inventory.global.security.oauth2.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class GoogleOAuth2UserUnlink implements OAuth2UserUnlink {

    private static final String URL = "https://oauth2.googleapis.com/revoke";
    private final RestTemplate restTemplate;


    @Override
    public void unlink(String accessToken) {

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("token", accessToken);
        restTemplate.postForObject(URL, params, String.class);
    }
}
