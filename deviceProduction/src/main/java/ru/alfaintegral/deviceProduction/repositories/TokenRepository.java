package ru.alfaintegral.deviceProduction.repositories;

import lombok.*;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.alfaintegral.deviceProduction.models.entities.Token;

public interface TokenRepository extends JpaRepository<Token, Long> {
}
