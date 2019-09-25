package ru.otus;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import ru.otus.cache.Cache;
import ru.otus.cache.CacheImpl;
import ru.otus.model.User;
import ru.otus.service.DbService;
import ru.otus.service.UserDbService;
import ru.otus.util.SessionFactories;

@Configuration
@ComponentScan
@EnableWebMvc
@PropertySource("application.properties")
public class Config implements WebMvcConfigurer {

    @Value("${cache.max-elements}")
    private Integer maxElements;
    @Value("${cache.time.life}")
    private Integer lifeTime;
    @Value("${cache.time.idle}")
    private Integer idleTime;
    @Value("${cache.eternal}")
    private Boolean isEternal;

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
        viewResolver.setPrefix("/templates/");
        viewResolver.setSuffix(".html");
        return viewResolver;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/templates/**").addResourceLocations("/templates/");
    }
}
