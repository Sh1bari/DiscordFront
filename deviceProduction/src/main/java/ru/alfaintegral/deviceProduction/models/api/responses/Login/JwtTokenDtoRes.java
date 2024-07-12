package ru.alfaintegral.deviceProduction.models.api.responses.Login;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JwtTokenDtoRes {
    private String access;
    private String refresh;
}
