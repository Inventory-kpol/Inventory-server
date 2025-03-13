package kpol.Inventory.domain.board.dto.req;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class BoardImageRequestDto {

    private List<MultipartFile> images;
}
