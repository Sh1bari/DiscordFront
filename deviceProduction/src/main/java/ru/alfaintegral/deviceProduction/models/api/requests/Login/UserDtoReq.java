package ru.alfaintegral.deviceProduction.models.api.requests.Login;

import lombok.*;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDtoReq {
    private String mail;
    private String password;
}
