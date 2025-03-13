package kpol.Inventory.domain.member.dto.res;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SignupResponseDto {

    private final String email;
    private final String nickname;
}
