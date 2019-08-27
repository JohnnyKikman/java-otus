package ru.otus.service;

import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import ru.otus.cache.Cache;
import ru.otus.model.User;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.Collection;
import java.util.Optional;

import static java.lang.String.format;

@RequiredArgsConstructor
public class UserDbService implements DbService<User> {

    private static final String ENTITY_HAS_NO_ID =
            "Сущность для обновления не имеет идентификатора, используйте метод для сохранения: %s";
    private static final String ENTITY_NOT_EXISTS_BY_ID = "Сущность типа %s с идентификатором %d не существует";
    private static final String NO_ENTITIES = "Не найдено ни одной сущности типа %s";

    private static final String SAVED_TEMPLATE = "Объект успешно сохранен: %s";
    private static final String UPDATED_TEMPLATE = "Объект успешно обновлен: %s";
    private static final String SELECTED_TEMPLATE = "Объект(ы) получен(ы): %s";

    private final Cache<Long, User> cache;
    private final SessionFactory sessionFactory;

    /**
     * {@inheritDoc}.
     */
    @Override
    public void create(User objectData) {
        try (final Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            session.save(objectData);

            session.getTransaction().commit();
            cache.put(objectData.getId(), objectData);
            System.out.println(format(SAVED_TEMPLATE, objectData));
        }
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void update(User objectData) {
        final Long id = objectData.getId();
        if (id == null) {
            throw new IllegalStateException(format(ENTITY_HAS_NO_ID, objectData));
        }
        try (final Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            session.update(objectData);

            session.getTransaction().commit();
            cache.put(id, objectData);
            System.out.println(format(UPDATED_TEMPLATE, objectData));
        }
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public User load(long id) {
        final Optional<User> resultFromCache = cache.get(id);
        if (resultFromCache.isPresent()) {
            return resultFromCache.get();
        }

        try (final Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            final User result = session.get(User.class, id);

            session.getTransaction().commit();
            if (result == null) {
                System.out.println(format(ENTITY_NOT_EXISTS_BY_ID, User.class, id));
            } else {
                System.out.println(format(SELECTED_TEMPLATE, result));
            }
            return result;
        }
    }

    @Override
    public Collection<User> loadAll() {
        // caching is not used in this case for simplicity
        try (final Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            final CriteriaBuilder cb = session.getCriteriaBuilder();
            final CriteriaQuery<User> cq = cb.createQuery(User.class);
            final Root<User> rootEntry = cq.from(User.class);
            final CriteriaQuery<User> getAll = cq.select(rootEntry);
            final TypedQuery<User> getAllQuery = session.createQuery(getAll);

            final Collection<User> result = getAllQuery.getResultList();

            session.getTransaction().commit();
            if (result.isEmpty()) {
                System.out.println(format(NO_ENTITIES, User.class));
            } else {
                System.out.println(format(SELECTED_TEMPLATE, result));
            }
            return result;
        }
    }
}
