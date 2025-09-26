package rca.risbo.E_com.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import rca.risbo.E_com.entity.User;

@Aspect
@Component
public class AdminOnlyAspect {

    @Around("@annotation(rca.risbo.E_com.annotation.AdminOnly)")
    public Object checkAdminAccess(ProceedingJoinPoint joinPoint) throws Throwable {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !(auth.getPrincipal() instanceof User)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User user = (User) auth.getPrincipal();
        if (!user.isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Accès réservé aux administrateurs");
        }

        return joinPoint.proceed();
    }
}
