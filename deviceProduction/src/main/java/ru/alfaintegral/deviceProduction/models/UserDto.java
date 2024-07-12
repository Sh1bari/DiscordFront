package ru.alfaintegral.deviceProduction.models;

import lombok.*;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private String username;
    private String mail;
}
