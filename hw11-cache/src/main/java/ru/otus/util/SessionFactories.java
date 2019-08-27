package ru.otus.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import ru.otus.model.Account;
import ru.otus.model.AddressDataSet;
import ru.otus.model.PhoneDataSet;
import ru.otus.model.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SessionFactories {

    private static final String CONFIG_FILE_PATH = "hibernate.cfg.xml";

    public static SessionFactory get() {
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

        return metadata.getSessionFactoryBuilder().build();
    }

}
