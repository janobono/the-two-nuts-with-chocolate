package sk.janobono.quarkusnut.repository;

import sk.janobono.quarkusnut.domain.Role;
import sk.janobono.quarkusnut.domain.RoleName;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;

@ApplicationScoped
public class RoleRepository {

    @Inject
    EntityManager em;

    public Role findByName(RoleName name) {
        return (Role) em.createQuery(
                "SELECT r FROM Role r WHERE r.name = :roleName")
                .setParameter("roleName", name)
                .getSingleResult();
    }

    @Transactional
    public void save(Role role) {
        em.persist(role);
        em.flush();
    }
}
