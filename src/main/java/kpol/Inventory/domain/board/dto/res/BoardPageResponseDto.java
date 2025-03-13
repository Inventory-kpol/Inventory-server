package kpol.Inventory.domain.board.dto.res;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

@Getter
@Setter
public class BoardPageResponseDto {
    private Page<BoardDetailResponseDto> boards;
}
