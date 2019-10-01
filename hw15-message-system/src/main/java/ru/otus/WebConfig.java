package ru.otus;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.PropertySource;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import ru.otus.broker.MessageSystem;
import ru.otus.broker.MessageSystemImpl;
import ru.otus.cache.Cache;
import ru.otus.cache.CacheImpl;
import ru.otus.model.User;
import ru.otus.service.DatabaseService;
import ru.otus.service.DbService;
import ru.otus.service.FrontendService;
import ru.otus.service.UserDbService;
import ru.otus.util.SessionFactories;

@Configuration
@ComponentScan
@EnableWebMvc
@PropertySource("application.properties")
public class WebConfig implements WebMvcConfigurer {

    @Value("${cache.max-elements}")
    private int maxElements;
    @Value("${cache.time.life}")
    private int lifeTime;
    @Value("${cache.time.idle}")
    private int idleTime;
    @Value("${cache.eternal}")
    private boolean isEternal;

    @Value("${destinations.db}")
    private String dbDestination;
    @Value("${destinations.frontend}")
    private String frontendDestination;

    @Bean
    public DbService<User> userDbService() {
        return new UserDbService(userCache(), sessionFactory());
    }

    @Bean
    public Cache<Long, User> userCache() {
        return new CacheImpl<>(maxElements, lifeTime, idleTime, isEternal);
    }

    @Bean
    public SessionFactory sessionFactory() {
        return SessionFactories.get();
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("index");
    }

    @Bean
    public ViewResolver viewResolver() {
        final InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/static/");
        viewResolver.setSuffix(".html");
        return viewResolver;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**")
                .addResourceLocations("/static/");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("/webjars/")
                .resourceChain(false);
    }

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public MessageSystem messageSystem() {
        return new MessageSystemImpl();
    }

    @Bean
    public DatabaseService databaseService(MessageSystem messageSystem) {
        final DatabaseService databaseService = new DatabaseService(userDbService(), messageSystem);
        messageSystem.registerReceiver(dbDestination, databaseService);
        return databaseService;
    }

    @Bean
    @DependsOn("databaseService")
    public FrontendService frontendService(MessageSystem messageSystem, SimpMessagingTemplate template,
                                           ObjectMapper objectMapper) {
        final FrontendService frontendService = new FrontendService(objectMapper, template);
        messageSystem.registerReceiver(frontendDestination, frontendService);
        messageSystem.start();
        return frontendService;
    }
}
