package ru.otus.server;

import com.google.gson.GsonBuilder;
import lombok.SneakyThrows;
import org.eclipse.jetty.security.ConstraintMapping;
import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.security.HashLoginService;
import org.eclipse.jetty.security.SecurityHandler;
import org.eclipse.jetty.security.authentication.BasicAuthenticator;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.security.Constraint;
import ru.otus.cache.CacheImpl;
import ru.otus.model.User;
import ru.otus.service.DbService;
import ru.otus.service.UserDbService;
import ru.otus.servlet.UserServlet;
import ru.otus.util.SessionFactories;

import java.net.URL;
import java.util.Collections;

import static java.lang.String.format;

public class UserServer {

    private static final int DEFAULT_PORT = 8080;

    private static final String USER_PATH = "/user";

    private static final String RESOURCES_PATH = "static";
    private static final String REALM_PATH = "realm.properties";
    private static final String INVALID_PATH = "Указан несуществующий путь к файлу: %s";

    @SneakyThrows
    public void start() {
        final Server server = createServer(DEFAULT_PORT);
        server.start();
        server.join();
    }

    @SuppressWarnings("SameParameterValue")
    private Server createServer(int port) {
        final ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);

        context.setBaseResource(Resource.newResource(getUrlByPath(RESOURCES_PATH)));
        context.addServlet(new ServletHolder("default", DefaultServlet.class), "/");
        context.addServlet(new ServletHolder(new UserServlet(new GsonBuilder().create(), createDbService())), USER_PATH);
        context.setSecurityHandler(createSecurityHandler());

        final Server server = new Server(port);
        final HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[]{
                context,
                createResourceHandler()
        });
        server.setHandler(handlers);

        return server;
    }

    private DbService<User> createDbService() {
        return new UserDbService(
                new CacheImpl<>(1, 0, 0, true),
                SessionFactories.get()
        );
    }

    private ResourceHandler createResourceHandler() {
        final ResourceHandler resourceHandler = new ResourceHandler();

        resourceHandler.setResourceBase(getUrlByPath(RESOURCES_PATH).getPath());
        resourceHandler.setDirectoriesListed(false);

        return resourceHandler;
    }

    private SecurityHandler createSecurityHandler() {
        final Constraint constraint = new Constraint();
        constraint.setName("auth");
        constraint.setAuthenticate(true);
        constraint.setRoles(new String[]{"all"});

        final ConstraintMapping mapping = new ConstraintMapping();
        mapping.setPathSpec("/*");
        mapping.setConstraint(constraint);

        final ConstraintSecurityHandler securityHandler = new ConstraintSecurityHandler();
        securityHandler.setAuthenticator(new BasicAuthenticator());
        final URL realmUrl = getUrlByPath(REALM_PATH);
        securityHandler.setLoginService(new HashLoginService("UserRealm", realmUrl.getPath()));
        securityHandler.setConstraintMappings(Collections.singletonList(mapping));

        return securityHandler;
    }

    private URL getUrlByPath(String pathString) {
        final URL pathUrl = this.getClass().getClassLoader().getResource(pathString);
        if (pathUrl == null) {
            throw new IllegalArgumentException(format(INVALID_PATH, pathString));
        }
        return pathUrl;
    }

}
