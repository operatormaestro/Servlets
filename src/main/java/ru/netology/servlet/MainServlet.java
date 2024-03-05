package ru.netology.servlet;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.netology.controller.PostController;
import ru.netology.service.PostService;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MainServlet extends HttpServlet {
    private final Map<String, Map<String, Handler>> handlers = new ConcurrentHashMap<>();
    private static final String pathPosts = "/api/posts";
    private static final String pathPostsSlash = "/api/posts/";
    private static final String regex = "/api/posts/\\d+";

    @Override
    public void init() {
        final var context = new AnnotationConfigApplicationContext("ru.netology");
        final PostController controller = (PostController) context.getBean("postController");
        context.getBean(PostService.class);

        addHandler(HTTPMethods.GET, pathPosts, (path, req, resp) -> {
            try {
                controller.all(resp);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        addHandler(HTTPMethods.GET, pathPostsSlash, (path, req, resp) -> {
            final var id = Long.parseLong(path.substring(path.lastIndexOf("/") + 1));
            controller.getById(id, resp);
        });
        addHandler(HTTPMethods.POST, pathPosts, (path, req, resp) -> controller.save(req.getReader(), resp));
        addHandler(HTTPMethods.DELETE, pathPostsSlash, (path, req, resp) -> {
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
            if (path.startsWith(pathPostsSlash) && path.matches(regex)) {
                handler = handlers.get(method).get(pathPostsSlash);
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

