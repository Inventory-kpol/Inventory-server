package kpol.Inventory.domain.board.dto.res;

import kpol.Inventory.domain.board.entity.Board;
import lombok.Getter;

@Getter
public class BoardListResponseDto {
    private final Long id;
    private final String title;

    public BoardListResponseDto(Board board) {
        this.id = board.getId();
        this.title = board.getTitle();
    }
}
