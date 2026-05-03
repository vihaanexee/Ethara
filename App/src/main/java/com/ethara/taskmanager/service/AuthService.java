package com.ethara.taskmanager.service;

import com.ethara.taskmanager.dto.AuthResponse;
import com.ethara.taskmanager.dto.LoginRequest;
import com.ethara.taskmanager.dto.SignupRequest;
import com.ethara.taskmanager.exception.BadRequestException;
import com.ethara.taskmanager.exception.DuplicateResourceException;
import com.ethara.taskmanager.model.User;
import com.ethara.taskmanager.repository.UserRepository;
import com.ethara.taskmanager.security.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
    }

    public AuthResponse signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already registered: " + request.getEmail());
        }
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(User.Role.MEMBER)
                .build();
        userRepository.save(user);
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        return AuthResponse.builder().token(token).email(user.getEmail())
                .name(user.getName()).role(user.getRole().name()).message("Signup successful").build();
    }

    public AuthResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        } catch (BadCredentialsException e) {
            throw new BadRequestException("Invalid email or password");
        }
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("User not found"));
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        return AuthResponse.builder().token(token).email(user.getEmail())
                .name(user.getName()).role(user.getRole().name()).message("Login successful").build();
    }
}
