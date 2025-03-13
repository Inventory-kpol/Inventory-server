package kpol.Inventory.domain.comment.dto.res;


import kpol.Inventory.domain.comment.entity.Comment;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter


public class CommentResponseDto {
    private Long id; //댓글 고유 id
    private String content; // 댓글 내용
    private LocalDateTime createdAt; //댓글 생성 시간
    private LocalDateTime updatedAt; //댓글 수정 시간
    private Long memberId; // 댓글 작성자의 id
    private Long boardId; // 댓글이 있는 게시물의 id
    private Long parentCommentId; // 부모 댓글의 id (대댓글인 경우)
    private List<CommentResponseDto> replies; // 대댓글 목록 저장 (재귀?)

    public CommentResponseDto(Long id, String content, LocalDateTime createdAt, LocalDateTime updatedAt
    ,Long memberId, Long boardId, Long parentCommentId, List<CommentResponseDto> replies) {
        this.id = id;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.memberId = memberId;
        this.boardId = boardId;
        this.parentCommentId = parentCommentId;
        this.replies = replies;
    }
}
