package com.WorksIn.config;


import com.WorksIn.entity.Member;
import com.WorksIn.entity.User;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

@Getter
@Setter
@ToString
public class OAuthAttributes {
    private Map<String, Object> attributes;
    private String nameAttributeKey;
    private String name;
    private String email;
    private String picture;


    public OAuthAttributes(Map<String, Object> attributes, String nameAttributeKey, String name,
                           String email, String picture) {

        this.attributes = attributes;
        this.nameAttributeKey = nameAttributeKey;
        this.name = name;
        this.email = email;
        this.picture = picture;
    }


    public OAuthAttributes() {

    }

    public static OAuthAttributes of(String registrationId, String userNameAttributeName, Map<String, Object> attributes) {
        if ("kakao".equals(registrationId)) {
            return ofKakao(userNameAttributeName, attributes);
        }
        if ("naver".equals(registrationId)) {
            return ofNaver(userNameAttributeName, attributes);

        }
        return ofGoogle(userNameAttributeName, attributes);
    }


    private static OAuthAttributes ofNaver(String userNameAttributeName,
                                           Map<String, Object> attributes) {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");    // 네이버에서 받은 데이터에서 프로필 정보다 담긴 response 값을 꺼낸다.


        return new OAuthAttributes(attributes,
                userNameAttributeName,
                (String) response.get("name"),
                (String) response.get("email"),
                (String) response.get("profile_image")
        );

    }

    private static OAuthAttributes ofKakao(String userNameAttributeName, Map<String, Object> attributes) {
        // 카카오는 kakao_account에 유저정보가 있음.(email)
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        // kakao_account안에 또 profile이라는 JSON 객체가 있음.(nickname, profile_image)
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
        return new OAuthAttributes(attributes,
                userNameAttributeName,
                (String) profile.get("nickname"),
                (String) kakaoAccount.get("email"),
                (String) profile.get("profile_image_url"));
    }

    private static OAuthAttributes ofGoogle(String userNameAttributeName, Map<String, Object> attributes) {
        return new OAuthAttributes(attributes, userNameAttributeName,
                (String) attributes.get("name"), (String) attributes.get("email"), (String) attributes.get("picture"));

    }

    public Member toEntity() {
        return new Member(name, email, picture);
    }

}
