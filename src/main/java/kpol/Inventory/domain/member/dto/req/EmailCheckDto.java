package kpol.Inventory.domain.member.dto.req;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class EmailCheckDto {
    @Email
    @NotEmpty(message = "이메일이 비어있습니다.")
    private String Email;

    @NotEmpty(message = "인증 번호가 비어있습니다.")
    private String authNum;

}
