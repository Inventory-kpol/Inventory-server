package kpol.Inventory.domain.board.dto.res;

import kpol.Inventory.domain.board.entity.Board;
import kpol.Inventory.domain.board.entity.BoardImage;
import kpol.Inventory.domain.comment.dto.res.CommentResponseDto;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
    private final List<String> imageUrls;
    private final List<CommentResponseDto> comments;

    public BoardDetailResponseDto(Board board) {
        this.nickname = board.getMember().getNickname();
        this.title = board.getTitle();
        this.content = board.getContent();
        this.createdAt = board.getCreatedAt();
        this.updatedAt = board.getUpdatedAt();
        this.viewCount = board.getViewCount();
        this.likeCount = board.getLikeCount();
        this.boardTags = new HashSet<>(board.getTagsName());
        this.imageUrls = board.getBoardImages().stream()
                .map(BoardImage::getUrl)
                .collect(Collectors.toList());
        this.comments = board.getComments().stream()
                .filter(comment -> comment.getParentComment() == null) // 부모 댓글만 필터링
                .map(CommentResponseDto::new)
                .collect(Collectors.toList());
    }

    public static Page<BoardDetailResponseDto> toDtoPage(Page<Board> boardPage) {
        return boardPage.map(BoardDetailResponseDto::new);
    }
}
