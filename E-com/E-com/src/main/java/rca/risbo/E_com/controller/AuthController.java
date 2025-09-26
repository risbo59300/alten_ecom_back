package rca.risbo.E_com.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import rca.risbo.E_com.DTO.AuthDto;
import rca.risbo.E_com.entity.User;
import rca.risbo.E_com.services.JwtService;
import rca.risbo.E_com.services.UserService;

@RestController
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;


    @PostMapping("/account")
    public ResponseEntity<AuthDto.TokenResponse> register(@Valid @RequestBody AuthDto.RegisterRequest request) {
        User user = new User(request.getUsername(), request.getFirstname(),
                request.getEmail(), request.getPassword());
        User createdUser = userService.createUser(user);

        String token = jwtService.generateToken(createdUser.getEmail());
        AuthDto.TokenResponse response = new AuthDto.TokenResponse(token, createdUser.getEmail(),
                createdUser.getUsername(), createdUser.isAdmin());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/token")
    public ResponseEntity<AuthDto.TokenResponse> login(@Valid @RequestBody AuthDto.LoginRequest request) {
        User user = userService.findByEmail(request.getEmail());

        if (!userService.validatePassword(user, request.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = jwtService.generateToken(user.getEmail());
        AuthDto.TokenResponse response = new AuthDto.TokenResponse(token, user.getEmail(),
                user.getUsername(), user.isAdmin());

        return ResponseEntity.ok(response);
    }
}
