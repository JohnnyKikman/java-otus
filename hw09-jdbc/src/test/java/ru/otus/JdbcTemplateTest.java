package ru.otus;

import lombok.SneakyThrows;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.otus.model.Account;
import ru.otus.model.User;
import ru.otus.service.JdbcTemplate;
import ru.otus.service.JdbcTemplateImpl;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

@DisplayName("JdbcTemplate")
class JdbcTemplateTest implements WithAssertions {

    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    @SneakyThrows
    void setUp() {
        final Connection connection = DriverManager.getConnection("jdbc:h2:mem:");
        connection.setAutoCommit(false);

        final PreparedStatement createUser = connection.prepareStatement(
                "CREATE TABLE User (" +
                        "id BIGINT(20) NOT NULL auto_increment, " +
                        "name VARCHAR(255), " +
                        "age INT(3)" +
                        ");"
        );
        final PreparedStatement createAccount = connection.prepareStatement(
                "CREATE TABLE Account (" +
                        "no BIGINT(20) NOT NULL auto_increment, " +
                        "type VARCHAR(255), " +
                        "rest NUMBER" +
                        ");"
        );

        try (createUser; createAccount) {
            createUser.executeUpdate();
            createAccount.executeUpdate();
        }
        connection.commit();

        jdbcTemplate = new JdbcTemplateImpl(connection);
    }

    @Test
    @DisplayName("Should create and select User")
    void shouldCreateAndSelectUser() {
        final User user = new User(1L, "John", 22);

        jdbcTemplate.create(user);

        assertThat(jdbcTemplate.load(1L, User.class)).isEqualTo(user);
    }

    @Test
    @DisplayName("Should create and select Account")
    void shouldCreateAndSelectAccount() {
        final Account account = new Account(2L, "Max", new BigDecimal("21.0"));

        jdbcTemplate.create(account);

        assertThat(jdbcTemplate.load(2L, Account.class)).isEqualTo(account);
    }

    @Test
    @DisplayName("Should create User with null fields")
    void shouldCreateUserWithNullFields() {
        final User user = new User(null, null, null);

        jdbcTemplate.create(user);

        final User userWithAutoId = new User(1L, null, null);
        assertThat(jdbcTemplate.load(1L, User.class)).isEqualTo(userWithAutoId);
    }

    @Test
    @DisplayName("Should create Account with null fields")
    void shouldCreateAccountWithNullFields() {
        final Account account = new Account(null, null, null);

        jdbcTemplate.create(account);

        final Account accountWithAutoId = new Account(1L, null, null);
        assertThat(jdbcTemplate.load(1L, Account.class)).isEqualTo(accountWithAutoId);
    }

    @Test
    void shouldUpdateExistingUser() {
        final User user = new User(1L, "Max", 21);

        jdbcTemplate.create(user);

        final User updatedUser = new User(1L, "Dan", 30);

        jdbcTemplate.update(updatedUser);

        assertThat(jdbcTemplate.load(1L, User.class)).isEqualTo(updatedUser);
    }

    @Test
    @DisplayName("Should update existing Account")
    void shouldUpdateExistingAccount() {
        final Account account = new Account(2L, "Active", new BigDecimal("11.11"));

        jdbcTemplate.create(account);

        final Account updatedAccount = new Account(2L, "Inactive", new BigDecimal("22.22"));

        jdbcTemplate.update(updatedAccount);

        assertThat(jdbcTemplate.load(2L, Account.class)).isEqualTo(updatedAccount);
    }

    @Test
    @DisplayName("Should fail selecting User by non-existing id")
    void shouldFailSelectingUserByNonExistingId() {
        assertThat(jdbcTemplate.load(111L, User.class)).isNull();
    }

    @Test
    @DisplayName("Should fail operation for entity withoud id")
    void shouldFailOperationForEntityWithoutId() {
        assertThatThrownBy(() -> jdbcTemplate.create(new Object()));
    }

}
