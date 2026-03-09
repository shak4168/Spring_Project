package com.project.web.config.jwt;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value; // 롬복 아님! 스프링꺼
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class JwtTokenProvider {

    // application.yml 등에 설정한 비밀키 (없으면 임의의 문자열 사용됨)
    // 실무에선 꼭 application.yml에 jwt.secret 설정을 넣어야 합니다.
    @Value("${jwt.secret:default_secret_key_must_be_very_long_to_prevent_errors}") 
    private String secretKey;

    // 토큰 유효시간 1시간 (밀리세컨드 단위)
    private long tokenValidTime = 60 * 60 * 1000L;

    private final UserDetailsService userDetailsService;


    //  토큰 생성 (로그인 시 사용)
    public String createToken(String userPk, String role) {
        Claims claims = Jwts.claims().setSubject(userPk); // JWT payload 에 저장되는 정보단위
        claims.put("role", role); // 정보는 key / value 쌍으로 저장된다.
        
        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims) // 정보 저장
                .setIssuedAt(now) // 토큰 발행 시간 정보
                .setExpiration(new Date(now.getTime() + tokenValidTime)) // set Expire Time
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()), SignatureAlgorithm.HS256) // 사용할 암호화 알고리즘과 signature 에 들어갈 secret값 세팅
                .compact();
    }

    //  인증 정보 조회 (필터에서 사용)
    // 토큰에서 회원 정보 추출 -> UserDetails 객체로 만듦 -> Spring Security가 이해하는 Authentication 객체로 변환
    public Authentication getAuthentication(String token) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(this.getUserPk(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    //  토큰에서 회원 정보 추출 (이메일 등)
    public String getUserPk(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateToken(String jwtToken) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(secretKey.getBytes())
                .build()
                .parseClaimsJws(jwtToken);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}