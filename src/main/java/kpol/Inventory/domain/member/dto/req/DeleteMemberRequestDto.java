package kpol.Inventory.domain.member.dto.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class DeleteMemberRequestDto {

    @NotBlank(message = "비밀번호가 비어있습니다.")
    private String password;
}
