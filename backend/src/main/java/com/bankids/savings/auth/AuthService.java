package com.bankids.savings.auth;

import com.bankids.savings.config.JwtService;
import com.bankids.savings.shared.UserAccount;
import com.bankids.savings.shared.UserAccountRepository;
import jakarta.validation.Valid;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final UserAccountRepository repository;
    private final JwtService jwtService;

    public AuthService(AuthenticationManager authenticationManager,
                       PasswordEncoder passwordEncoder,
                       UserAccountRepository repository,
                       JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.repository = repository;
        this.jwtService = jwtService;
    }

    public AuthResponse login(@Valid LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        UserAccount account = repository.findByEmail(authentication.getName())
                .orElseThrow();
        String token = jwtService.generateToken(account.getGuardianEmail(), Map.of(
                "email", account.getGuardianEmail(),
                "child", account.getChildName()
        ));
        return new AuthResponse(token, account.getChildName(), account.getAge());
    }

    public AuthResponse register(@Valid RegisterRequest request) {
        repository.findByEmail(request.getGuardianEmail()).ifPresent(account -> {
            throw new IllegalArgumentException("Ya existe una cuenta con este correo");
        });
        UserAccount account = new UserAccount(
                request.getChildName(),
                request.getAge(),
                request.getGuardianName(),
                request.getGuardianEmail(),
                passwordEncoder.encode(request.getPassword())
        );
        repository.save(account);
        String token = jwtService.generateToken(account.getGuardianEmail(), Map.of(
                "email", account.getGuardianEmail(),
                "child", account.getChildName()
        ));
        return new AuthResponse(token, account.getChildName(), account.getAge());
    }
}
