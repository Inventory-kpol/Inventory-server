package kpol.Inventory.domain.board.dto.res;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class BoardResponseDto {

    private final Long id;
    private final String title;
    private final String content;
    private final LocalDateTime createdAt;
    private final Integer likeCount;

    public BoardResponseDto(Long id, String title, String content, LocalDateTime createdAt, Integer likeCount) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.likeCount = likeCount;
    }
}
