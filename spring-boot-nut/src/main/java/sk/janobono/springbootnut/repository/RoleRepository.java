package sk.janobono.springbootnut.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sk.janobono.springbootnut.domain.Role;
import sk.janobono.springbootnut.domain.RoleName;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Role findByName(RoleName name);
}
