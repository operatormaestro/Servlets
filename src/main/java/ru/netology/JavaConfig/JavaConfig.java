package ru.netology.JavaConfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.netology.controller.PostController;
import ru.netology.repository.IPostRepository;
import ru.netology.repository.PostRepository;
import ru.netology.service.PostService;

@Configuration
public class JavaConfig {
    @Bean
    public PostController postController(PostService service) {
        return new PostController(service);
    }

    @Bean
    public PostService postService(IPostRepository repository) {
        return new PostService(repository);
    }

    @Bean
    public IPostRepository postRepository() {
        return new PostRepository();
    }
}
