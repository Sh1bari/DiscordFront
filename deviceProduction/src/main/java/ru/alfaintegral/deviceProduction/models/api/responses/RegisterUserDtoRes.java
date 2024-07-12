package ru.alfaintegral.deviceProduction.models.api.responses;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterUserDtoRes {
    @JsonUnwrapped
    private UserDtoRes user;
    private JwtTokenDtoRes jwtTokens;
}
