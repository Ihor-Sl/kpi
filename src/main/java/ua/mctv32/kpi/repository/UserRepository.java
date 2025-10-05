package ua.mctv32.kpi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.mctv32.kpi.domain.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
}
