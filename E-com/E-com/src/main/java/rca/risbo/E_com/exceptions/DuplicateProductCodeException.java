package rca.risbo.E_com.exceptions;

import jakarta.validation.constraints.NotBlank;

public class DuplicateProductCodeException extends RuntimeException {
    public DuplicateProductCodeException( String message) {
        super(message);
    }
}
