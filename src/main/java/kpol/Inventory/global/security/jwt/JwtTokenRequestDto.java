package kpol.Inventory.global.security.jwt;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class JwtTokenRequestDto {

    private String refreshToken;
}
