package ru.otus.servlet;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import ru.otus.model.User;
import ru.otus.service.DbService;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.Reader;

@RequiredArgsConstructor
public class UserServlet extends HttpServlet {

    private static final String APPLICATION_JSON = "application/json";

    private final Gson gson;
    private final DbService<User> dbService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        System.out.println("Request to get user list received");
        try (final PrintWriter writer = resp.getWriter()) {
            writer.print(gson.toJson(dbService.loadAll()));
            resp.setContentType(APPLICATION_JSON);
            resp.setStatus(HttpServletResponse.SC_OK);
            writer.flush();
            System.out.println("Successfully fetched user list");
        } catch (Exception e) {
            System.err.println("Fetching user list failed: " + e.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        System.out.println("Request to create user received");
        try (final Reader reader = req.getReader()) {
            dbService.create(gson.fromJson(reader, User.class));
            resp.setStatus(HttpServletResponse.SC_OK);
            System.out.println("Successfully created user");
        } catch (Exception e) {
            System.err.println("Creating user failed: " + e.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
