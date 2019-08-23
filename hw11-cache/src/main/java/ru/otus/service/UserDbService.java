package ru.otus.service;

import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import ru.otus.cache.Cache;
import ru.otus.model.User;

import static java.lang.String.format;

@RequiredArgsConstructor
public class UserDbService implements DbService<User> {

    private static final String ENTITY_HAS_NO_ID =
            "Сущность для обновления не имеет идентификатора, используйте метод для сохранения: %s";
    private static final String ENTITY_NOT_EXISTS = "Сущность типа %s с идентификатором %d не существует";

    private static final String SAVED_TEMPLATE = "Объект успешно сохранен: %s";
    private static final String UPDATED_TEMPLATE = "Объект успешно обновлен: %s";
    private static final String SELECTED_TEMPLATE = "Объект получен: %s";

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
            System.out.println(format(SAVED_TEMPLATE, objectData));
        }
        cache.put(objectData.getId(), objectData);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void update(User objectData) {
        if (objectData.getId() == null) {
            throw new IllegalStateException(format(ENTITY_HAS_NO_ID, objectData));
        }
        try (final Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            session.update(objectData);

            session.getTransaction().commit();
            System.out.println(format(UPDATED_TEMPLATE, objectData));
        }
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public User load(long id) {
        final User resultFromCache = cache.get(id);
        if (resultFromCache != null) {
            return resultFromCache;
        }

        try (final Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            final User result = session.get(User.class, id);

            session.getTransaction().commit();
            if (result == null) {
                System.out.println(format(ENTITY_NOT_EXISTS, User.class, id));
            } else {
                System.out.println(format(SELECTED_TEMPLATE, result));
            }
            return result;
        }
    }
}
