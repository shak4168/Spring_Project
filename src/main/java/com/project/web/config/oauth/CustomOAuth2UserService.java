package com.project.web.config.oauth;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.project.web.domain.member.Member;
import com.project.web.domain.member.Role;
import com.project.web.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 1. 구글 서비스에서 유저 정보 가져오기
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        // 2. 서비스 구분 (google)
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        // 3. PK 역할 (sub)
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        // 4. 유저 정보 (Attributes)
        Map<String, Object> attributes = oAuth2User.getAttributes();
        log.info("Social Login Info: {}", attributes);

        // 5. DB 저장 또는 업데이트
        saveOrUpdate(attributes, registrationId, userNameAttributeName);

        // 6. 세션용 객체 반환
        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                attributes,
                userNameAttributeName);
    }

    private Member saveOrUpdate(Map<String, Object> attributes, String provider, String providerIdKey) {
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");
        String providerId = (String) attributes.get(providerIdKey); // sub 값

        // 비밀번호는 랜덤 생성 (어차피 소셜 로그인은 비번 안 씀)
        String password = passwordEncoder.encode(UUID.randomUUID().toString());

        Member member = memberRepository.findByEmail(email)
                .map(entity -> entity.update(name)) // 이미 있으면 이름 업데이트
                .orElse(Member.builder()
                        .name(name)
                        .email(email)
                        .password(password)
                        .role(Role.USER)
                        .provider(provider)   // "google"
                        .providerId(providerId) // 구글 ID
                        .build());

        return memberRepository.save(member);
    }
}