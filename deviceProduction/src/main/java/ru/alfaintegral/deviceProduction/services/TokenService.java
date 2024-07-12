package ru.alfaintegral.deviceProduction.services;

import lombok.*;
import org.springframework.stereotype.Service;
import ru.alfaintegral.deviceProduction.models.api.responses.JwtTokenDtoRes;
import ru.alfaintegral.deviceProduction.models.entities.Token;
import ru.alfaintegral.deviceProduction.repositories.TokenRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final TokenRepository tokenRepo;

    public Token getToken(){
        Optional<Token> t = tokenRepo.findById(1L);
        if(t.isPresent()){
            return t.get();
        }else {
            Token newToken = new Token();
            return tokenRepo.save(newToken);
        }
    }

    public Token saveToken(JwtTokenDtoRes token) {
        Token t = getToken();
        t.setAccessToken(token.getAccess());
        t.setRefreshToken(token.getRefresh());
        return tokenRepo.save(t);
    }
}
