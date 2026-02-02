package com.project.web.dto.review;

import java.time.format.DateTimeFormatter;

import com.project.web.domain.review.Reply;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReplyResponseDTO {
    private Long replyId;
    private String content;
    private String writerName;
    private String regTime;

    public ReplyResponseDTO(Reply reply) {
        this.replyId = reply.getId();
        this.content = reply.getContent();
        this.writerName = reply.getMember().getName(); // Member에 getName()이 있어야 함
        // BaseEntity에 getRegTime()이 없으면 getCreatedAt()이나 getRegDate()를 쓰세요.
        // 여기서는 getRegTime()을 쓴다고 가정하고, 아래 BaseEntity 설명을 참고하세요.
        this.regTime = reply.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }
}