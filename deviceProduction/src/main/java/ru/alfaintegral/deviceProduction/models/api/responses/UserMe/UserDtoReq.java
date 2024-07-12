package ru.alfaintegral.deviceProduction.models.api.responses.UserMe;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import ru.alfaintegral.deviceProduction.models.enums.Role;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDtoReq {
    private UUID id;
    private String username;
    private String mail;
    private String name;
    private String middleName;
    private String surname;
    private UUID avatarId;
    private List<Role> roles;
}
