package kpol.Inventory.domain.member.dto.req;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberRequestDto {
    private String username;
    private String nickname;
    private String email;
    private String password;
}
