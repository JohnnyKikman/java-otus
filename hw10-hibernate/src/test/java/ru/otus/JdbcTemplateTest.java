package ru.otus;

import lombok.SneakyThrows;
import org.assertj.core.api.WithAssertions;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.otus.model.Account;
import ru.otus.model.AddressDataSet;
import ru.otus.model.PhoneDataSet;
import ru.otus.model.User;
import ru.otus.service.JdbcTemplate;
import ru.otus.service.JdbcTemplateImpl;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

@DisplayName("JdbcTemplate")
class JdbcTemplateTest implements WithAssertions {

    private static final String CONFIG_FILE_PATH = "hibernate.cfg.xml";

    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    @SneakyThrows
    void setUp() {
        final Configuration configuration = new Configuration().configure(CONFIG_FILE_PATH);

        final StandardServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                .applySettings(configuration.getProperties())
                .build();

        final Metadata metadata = new MetadataSources(serviceRegistry)
                .addAnnotatedClass(User.class)
                .addAnnotatedClass(AddressDataSet.class)
                .addAnnotatedClass(PhoneDataSet.class)
                .addAnnotatedClass(Account.class)
                .getMetadataBuilder().build();

        jdbcTemplate = new JdbcTemplateImpl(metadata.getSessionFactoryBuilder().build());
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

        jdbcTemplate.create(user);

        assertThat(jdbcTemplate.load(user.getId(), User.class)).isEqualToComparingFieldByFieldRecursively(user);
    }

    @Test
    @DisplayName("Should create and select Account")
    void shouldCreateAndSelectAccount() {
        final Account account = new Account();
        account.setType("Max");
        account.setRest(new BigDecimal("21.00"));

        jdbcTemplate.create(account);

        assertThat(jdbcTemplate.load(account.getId(), Account.class)).isEqualToComparingFieldByField(account);
    }

    @Test
    @DisplayName("Should create User with null fields")
    void shouldCreateUserWithNullFields() {
        final User user = new User();

        jdbcTemplate.create(user);

        assertThat(jdbcTemplate.load(user.getId(), User.class)).isEqualToComparingFieldByField(user);
    }

    @Test
    @DisplayName("Should create Account with null fields")
    void shouldCreateAccountWithNullFields() {
        final Account account = new Account();

        jdbcTemplate.create(account);

        assertThat(jdbcTemplate.load(account.getId(), Account.class)).isEqualToComparingFieldByField(account);
    }

    @Test
    void shouldUpdateExistingUser() {
        final User user = new User();
        user.setName("Max");
        user.setAge(21);
        user.setAddressDataSet(new AddressDataSet("ул. Валовая"));
        final List<PhoneDataSet> phoneDataSets = new ArrayList<>();
        phoneDataSets.add(new PhoneDataSet("84957654321"));
        phoneDataSets.add(new PhoneDataSet("84951111111"));
        user.setPhoneDataSets(phoneDataSets);

        jdbcTemplate.create(user);

        user.setName("Dan");
        user.getAddressDataSet().setStreet("ул. Житная");
        user.getPhoneDataSets().remove(phoneDataSets.get(0));

        jdbcTemplate.update(user);

        assertThat(jdbcTemplate.load(user.getId(), User.class)).isEqualToComparingFieldByFieldRecursively(user);
    }

    @Test
    @DisplayName("Should update existing Account")
    void shouldUpdateExistingAccount() {
        final Account account = new Account();
        account.setType("Active");
        account.setRest(new BigDecimal("11.11"));

        jdbcTemplate.create(account);

        account.setType("Inactive");
        account.setRest(new BigDecimal("22.22"));

        jdbcTemplate.update(account);

        assertThat(jdbcTemplate.load(account.getId(), Account.class)).isEqualTo(account);
    }

    @Test
    @DisplayName("Should fail selecting User by non-existing id")
    void shouldFailSelectingUserByNonExistingId() {
        assertThat(jdbcTemplate.load(111L, User.class)).isNull();
    }

}
