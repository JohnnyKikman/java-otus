package ru.otus.service;

import lombok.SneakyThrows;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.otus.cache.Cache;
import ru.otus.cache.CacheImpl;
import ru.otus.model.AddressDataSet;
import ru.otus.model.PhoneDataSet;
import ru.otus.model.User;
import ru.otus.util.SessionFactories;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

@DisplayName("DbService")
class DbServiceTest implements WithAssertions {

    private Cache<Long, User> cache;
    private DbService<User> userService;

    @BeforeEach
    @SneakyThrows
    void setUp() {
        cache = new CacheImpl<>(1, 0, 0, true);
        userService = new UserDbService(cache, SessionFactories.get());
    }

    @Test
    @DisplayName("Should create and select User")
    void shouldCreateAndSelectUser() {
        final User user = new User();
        user.setName("John");
        user.setAge(22);
        user.setAddressDataSet(new AddressDataSet("Садовое кольцо"));
        user.setPhoneDataSets(asList(
                new PhoneDataSet("84951000000"),
                new PhoneDataSet("88005553535")
        ));

        userService.create(user);

        assertThat(userService.load(user.getId())).isEqualToComparingFieldByFieldRecursively(user);

        assertThat(cache.getHitCount()).isEqualTo(1);
        assertThat(cache.getMissCount()).isEqualTo(0);
    }

    @Test
    @DisplayName("Should create and select multiple users")
    void shouldCreateAndSelectMultipleUsers() {
        final User firstUser = new User();
        firstUser.setName("Seb");
        firstUser.setAge(32);

        userService.create(firstUser);

        final User secondUser = new User();
        secondUser.setName("Lewis");
        secondUser.setAge(34);

        userService.create(secondUser);

        assertThat(userService.loadAll()).containsExactlyInAnyOrder(firstUser, secondUser);
    }

    @Test
    @DisplayName("Should create User with null fields")
    void shouldCreateUserWithNullFields() {
        final User user = new User();

        userService.create(user);

        assertThat(userService.load(user.getId())).isEqualToComparingFieldByField(user);

        assertThat(cache.getHitCount()).isEqualTo(1);
        assertThat(cache.getMissCount()).isEqualTo(0);
    }

    @Test
    @DisplayName("Should update existing User")
    void shouldUpdateExistingUser() {
        final User user = new User();
        user.setName("Max");
        user.setAge(21);
        user.setAddressDataSet(new AddressDataSet("ул. Валовая"));
        final List<PhoneDataSet> phoneDataSets = new ArrayList<>();
        phoneDataSets.add(new PhoneDataSet("84957654321"));
        phoneDataSets.add(new PhoneDataSet("84951111111"));
        user.setPhoneDataSets(phoneDataSets);

        userService.create(user);

        user.setName("Dan");
        user.getAddressDataSet().setStreet("ул. Житная");
        user.getPhoneDataSets().remove(phoneDataSets.get(0));

        userService.update(user);

        assertThat(userService.load(user.getId())).isEqualToComparingFieldByFieldRecursively(user);

        assertThat(cache.getHitCount()).isEqualTo(1);
        assertThat(cache.getMissCount()).isEqualTo(0);
    }

    @Test
    @DisplayName("Should get User with miss count increment")
    void shouldGetUserWithMissCountIncrement() {
        final User firstUser = new User();
        firstUser.setName("First");

        userService.create(firstUser);

        assertThat(userService.load(firstUser.getId())).isNotNull();

        assertThat(cache.getHitCount()).isEqualTo(1);
        assertThat(cache.getMissCount()).isEqualTo(0);

        final User secondUser = new User();
        secondUser.setName("Second");

        userService.create(secondUser);

        assertThat(userService.load(firstUser.getId())).isNotNull();
        assertThat(userService.load(secondUser.getId())).isNotNull();

        assertThat(cache.getHitCount()).isEqualTo(2);
        assertThat(cache.getMissCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should fail selecting User by non-existing id")
    void shouldFailSelectingUserByNonExistingId() {
        assertThat(userService.load(111L)).isNull();

        assertThat(cache.getHitCount()).isEqualTo(0);
        assertThat(cache.getMissCount()).isEqualTo(1);
    }

}
