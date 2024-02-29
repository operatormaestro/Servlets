package ru.netology.servlet;

import ru.netology.controller.PostController;
import ru.netology.repository.PostRepository;
import ru.netology.service.PostService;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MainServlet extends HttpServlet {
    private PostController controller;
    private final Map<String, Map<String, Handler>> handlers = new ConcurrentHashMap<>();

    @Override
    public void init() {
        final var repository = new PostRepository();
        final var service = new PostService(repository);
        controller = new PostController(service);

        addHandler(HTTPMethods.GET, "/api/posts", (path, req, resp) -> {
            try {
                controller.all(resp);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        addHandler(HTTPMethods.GET, "/api/posts/", (path, req, resp) -> {
            final var id = Long.parseLong(path.substring(path.lastIndexOf("/") + 1));
            controller.getById(id, resp);
        });
        addHandler(HTTPMethods.POST, "/api/posts", (path, req, resp) -> controller.save(req.getReader(), resp));
        addHandler(HTTPMethods.DELETE, "/api/posts/", (path, req, resp) -> {
            final var id = Long.parseLong(path.substring(path.lastIndexOf("/") + 1));
            controller.removeById(id, resp);
        });
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) {
        try {
            final var path = req.getRequestURI();
            final var method = req.getMethod();

            Handler handler;
            if (path.startsWith("/api/posts/") && path.matches("/api/posts/\\d+")) {
                handler = handlers.get(method).get("/api/posts/");
            } else {
                handler = handlers.get(method).get(path);
            }
            if (handler == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            handler.handle(path, req, resp);

        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    public void addHandler(HTTPMethods method, String path, Handler handler) {
        String stringMethod = method.getTitle();
        Map<String, Handler> map = new ConcurrentHashMap<>();
        if (handlers.containsKey(stringMethod)) {
            map = handlers.get(stringMethod);
        }
        map.put(path, handler);
        handlers.put(stringMethod, map);
    }
}

