package kpol.Inventory.domain.member.dto.req;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

@Getter
public class EmailRequestDto {

    @Email
    @NotEmpty(message = "이메일이 비어있습니다.")
    private String email;

}
