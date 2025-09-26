package rca.risbo.E_com.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AuthDto {

    public static class LoginRequest {

        @Email(message = "Email invalide")
        @NotBlank(message = "L'email est obligatoire")
        private String email;

        @NotBlank(message = "Le mot de passe est obligatoire")
        private String password;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    public static class RegisterRequest{

        @NotBlank(message = "Le nom est obligatoire")
        private String username;

        @NotBlank(message = "Le prénom est obligatoire")
        private String firstname;

        @Email(message = "Email invalide")
        @NotBlank(message = "L'email est obligatoire")
        private String email;

        @NotBlank(message = "Le mot de passe est obligatoire")
        @Size(min = 6, message = "Le mot de passe doit contenir au moins 6 caractères")
        private String password;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getFirstname() {
            return firstname;
        }

        public void setFirstname(String firstname) {
            this.firstname = firstname;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    public static class TokenResponse{
        private String token;
        private String email;
        private String username;
        private boolean isAdmin;

        public TokenResponse(String token, String email, String username, boolean isAdmin) {
            this.token = token;
            this.email = email;
            this.username = username;
            this.isAdmin = isAdmin;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public boolean isAdmin() {
            return isAdmin;
        }

        public void setAdmin(boolean admin) {
            isAdmin = admin;
        }
    }

}
