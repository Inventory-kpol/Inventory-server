package kpol.Inventory.global.security.oauth2.user;

import java.util.Map;

public class NaverOAuth2UserInfo implements OAuth2UserInfo {

    private final Map<String, Object> attributes;

    private final String accessToken;
    private final String id;
    private final String email;
    private final String name;
    private final String firstName;
    private final String lastName;
    private final String nickname;
    private final String profileUrl;

    public NaverOAuth2UserInfo(Map<String, Object> attributes, String accessToken) {
        this.attributes = (Map<String, Object>) attributes.get("response");
        this.accessToken = accessToken;
        this.id = (String) this.attributes.get("id");
        this.email = (String) this.attributes.get("email");
        this.name = (String) this.attributes.get("name");
        this.firstName = null;
        this.lastName = null;
        this.nickname = (String) attributes.get("nickname");
        this.profileUrl = (String) attributes.get("profile_image");

        this.attributes.put("id", id);
        this.attributes.put("email", this.email);
    }

    @Override
    public OAuth2Provider getProvider() { return OAuth2Provider.NAVER; }

    @Override
    public String getAccessToken() { return accessToken; }

    @Override
    public Map<String, Object> getAttributes() { return attributes; }

    @Override
    public String getId() { return id; }

    @Override
    public String getName() { return name; }

    @Override
    public String getEmail() { return email; }

    @Override
    public String getFirstName() { return firstName; }

    @Override
    public String getLastName() { return lastName; }

    @Override
    public String getNickname() { return nickname; }

    @Override
    public String getProfileUrl() { return profileUrl; }
}
