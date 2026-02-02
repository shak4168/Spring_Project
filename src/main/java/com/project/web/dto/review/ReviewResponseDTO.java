package com.project.web.dto.review;

import java.time.format.DateTimeFormatter;

import com.project.web.domain.review.Review;

import lombok.Getter;

@Getter
public class ReviewResponseDTO {

    private Long id;
    private String content;
    private int rating;
    private String writer; // 마스킹 된 작성자 (예: h***)
    private String regTime;
    
    // 답글이 있다면 함께 반환 (없으면 null)
    private ReplyResponseDTO reply; 

    public ReviewResponseDTO(Review review) {
        this.id = review.getId();
        this.content = review.getContent();
        this.rating = review.getRating();
        this.writer = maskEmail(review.getMember().getEmail()); // 마스킹 처리
        this.regTime = review.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        
        // 답글이 존재하면 DTO로 변환
        // (주의: 1:N인 경우 getReplies().stream()... 처리 필요. 현재는 1:N 구조지만 1개만 노출한다고 가정)
        if (review.getReplies() != null && !review.getReplies().isEmpty()) {
             // 가장 최근 답글 하나만 보여준다고 가정
             this.reply = new ReplyResponseDTO(review.getReplies().get(0));
        }
    }

    // 간단한 이메일 마스킹 메서드
    private String maskEmail(String email) {
        if (email == null || email.length() < 4) return email;
        return email.substring(0, 2) + "***";
    }
}