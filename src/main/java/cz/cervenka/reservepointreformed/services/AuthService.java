package cz.cervenka.reservepointreformed.services;

import cz.cervenka.reservepointreformed.database.entities.UserEntity;
import cz.cervenka.reservepointreformed.database.repositories.UserRepository;
import cz.cervenka.reservepointreformed.utils.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    public boolean userExists(String email)  {
        return userRepository.findByEmail(email).isPresent();
    }

    public void registerUser(UserEntity user) {
        if (userExists(user.getEmail())) {
            throw new IllegalArgumentException("User with this email already exists.");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreatedAt(LocalDateTime.now());

        if (userRepository.count() == 0) {
            user.setRole(UserEntity.Role.ADMIN);
        } else {
            user.setRole(UserEntity.Role.USER);
        }

        userRepository.save(user);
    }

    public Map<String, String> authenticateUser(String email, String password) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );

            String token = jwtUtil.generateToken(authentication.getName());
            return Map.of("token", token, "username", authentication.getName());
        } catch (Exception e) {
            throw new AuthenticationServiceException("Invalid username or password", e);
        }
    }
}
