package com.hiccproject.moaram.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KakaoUserInfoDto {

    @JsonProperty("id")  // id 필드는 그대로 두고, JSON에서의 "id"를 Java의 "id"로 매핑
    private Long id;

    @JsonProperty("connected_at")  // JSON의 "connected_at"을 "connectedAt"으로 매핑
    private String connectedAt;

    private Properties properties;

    @JsonProperty("kakao_account")
    private KakaoAccount kakaoAccount;

    // Properties 클래스 정의
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Properties {

        @JsonProperty("nickname")  // nickname 필드 매핑
        private String nickname;
    }

    // KakaoAccount 클래스 정의
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KakaoAccount {

        @JsonProperty("profile_nickname_needs_agreement")  // profile_nickname_needs_agreement 필드를 매핑
        private Boolean profileNicknameNeedsAgreement;

        private Profile profile;

        @JsonProperty("has_email")  // has_email 필드 매핑
        private Boolean hasEmail;

        @JsonProperty("email_needs_agreement")  // email_needs_agreement 필드 매핑
        private Boolean emailNeedsAgreement;

        @JsonProperty("is_email_valid")  // is_email_valid 필드 매핑
        private Boolean isEmailValid;

        @JsonProperty("is_email_verified")  // is_email_verified 필드 매핑
        private Boolean isEmailVerified;

        @JsonProperty("email")  // email 필드 매핑
        private String email;

        // Profile 클래스 정의
        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Profile {

            @JsonProperty("nickname")  // nickname 필드 매핑
            private String nickname;

            @JsonProperty("is_default_nickname")  // is_default_nickname 필드를 매핑
            private Boolean isDefaultNickname;
        }
    }
}
