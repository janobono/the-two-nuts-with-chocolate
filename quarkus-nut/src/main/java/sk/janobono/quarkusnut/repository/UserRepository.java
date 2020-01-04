package sk.janobono.quarkusnut.repository;

import sk.janobono.quarkusnut.domain.User;
import sk.janobono.quarkusnut.so.Pageable;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class UserRepository {

    @Inject
    EntityManager em;

    public boolean existsById(Long id) {
        return ((Long) em.createQuery("SELECT count(u.id) FROM User u WHERE u.id = :id")
                .setParameter("id", id)
                .getSingleResult()) == 1;
    }

    public boolean existsByEmail(String email) {
        return ((Long) em.createQuery("SELECT count(u.id) FROM User u WHERE u.email LIKE :email")
                .setParameter("email", email)
                .getSingleResult()) == 1;
    }

    public User findById(Long id) {
        return (User) em.createQuery(
                "SELECT u FROM User u WHERE u.id = :id")
                .setParameter("id", id)
                .getSingleResult();
    }

    public List<User> findAll(Pageable pageable) {
        String qlString = "SELECT u FROM User u";
        if (!pageable.isUnsorted()) {
            qlString += " ORDER BY u." + pageable.getSortField();
            if (pageable.getSortDirection() != Pageable.SortDirection.ASC) {
                qlString += " " + pageable.getSortDirection().name();
            }
        }

        Query query = em.createQuery(qlString);
        if (!pageable.isUnpaged()) {
            query.setFirstResult(pageable.getPageNumber() * pageable.getPageSize())
                    .setMaxResults(pageable.getPageSize());
        }
        return query.getResultList();
    }

    @Transactional
    public void save(User user) {
        em.persist(user);
        em.flush();
    }

    @Transactional
    public void deleteById(Long id) {
        em.createQuery("DELETE FROM User u WHERE u.id = :id")
                .setParameter("id", id)
                .executeUpdate();
    }
}
