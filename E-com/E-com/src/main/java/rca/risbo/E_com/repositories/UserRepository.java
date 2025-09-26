package rca.risbo.E_com.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rca.risbo.E_com.entity.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  Optional<User> findByEmail(String email);
  Optional<User> findByUsername(String username);
  boolean existsByEmail(String email);
  boolean existsByUsername(String username);

}