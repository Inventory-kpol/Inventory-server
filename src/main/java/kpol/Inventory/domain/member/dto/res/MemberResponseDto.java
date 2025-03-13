package kpol.Inventory.domain.member.dto.res;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Getter
@NoArgsConstructor
public class MemberResponseDto {
    private Long id;
    private String username;
    private String nickname;
    private String email;

}


