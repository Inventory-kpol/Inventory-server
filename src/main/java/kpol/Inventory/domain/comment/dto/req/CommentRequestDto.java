package kpol.Inventory.domain.comment.dto.req;


import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentRequestDto {

    private String content;  // 댓글 내용 저장
    private Long parentCommentId; // 대댓글 작성시, 부모 댓글의 id 저장 (단, 일반 댓글인 경우 =null)
}
