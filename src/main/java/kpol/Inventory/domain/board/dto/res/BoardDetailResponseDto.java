package kpol.Inventory.domain.board.dto.res;

import kpol.Inventory.domain.board.entity.Board;
import kpol.Inventory.domain.comment.entity.Comment;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
public class BoardDetailResponseDto {
    private final String nickname;
    private final String title;
    private final String content;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final int viewCount;
    private final int likeCount;
    private final Set<String> boardTags;
    private final List<Comment> comments;

    public BoardDetailResponseDto(Board board) {
        this.nickname = board.getMember().getNickname();
        this.title = board.getTitle();
        this.content = board.getContent();
        this.createdAt = board.getCreatedAt();
        this.updatedAt = board.getUpdatedAt();
        this.viewCount = board.getViewCount();
        this.likeCount = board.getLikeCount();
        this.boardTags = new HashSet<>(board.getTagsName());
        this.comments = new ArrayList<>();
    }

    public static Page<BoardDetailResponseDto> toDtoPage(Page<Board> boardPage) {
        return boardPage.map(BoardDetailResponseDto::new);
    }
}
