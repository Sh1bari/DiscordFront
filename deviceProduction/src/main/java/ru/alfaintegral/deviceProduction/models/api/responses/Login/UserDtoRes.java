package ru.alfaintegral.deviceProduction.models.api.responses.Login;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.alfaintegral.deviceProduction.models.enums.UserStatus;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class UserDtoRes {
    private UUID id;
    private String mail;
    private String username;
    private UserStatus status;
}
