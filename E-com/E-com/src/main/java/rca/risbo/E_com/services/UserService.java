package rca.risbo.E_com.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import rca.risbo.E_com.entity.User;
import rca.risbo.E_com.exceptions.DuplicateUserException;
import rca.risbo.E_com.exceptions.UserNotFoundException;
import rca.risbo.E_com.repositories.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User createUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new DuplicateUserException("Un utilisateur avec cet email existe déjà");
        }
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new DuplicateUserException("Un utilisateur avec ce nom d'utilisateur existe déjà");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Utilisateur non trouvé avec l'email: " + email));
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Utilisateur non trouvé avec l'ID: " + id));
    }

    public boolean validatePassword(User user, String rawPassword) {
        return passwordEncoder.matches(rawPassword, user.getPassword());
    }
}
