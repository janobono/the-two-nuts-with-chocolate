package sk.janobono.springbootnut.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sk.janobono.springbootnut.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);
}
