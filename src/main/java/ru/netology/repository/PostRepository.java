package ru.netology.repository;

import org.springframework.stereotype.Repository;
import ru.netology.model.Post;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class PostRepository implements IPostRepository {
    public Map<Long, Post> repositoryMap = new ConcurrentHashMap<>();
    public static AtomicLong postCounter = new AtomicLong(0);

    @Override
    public List<Post> all() {
        return new ArrayList<>(repositoryMap.values());
    }

    @Override
    public Optional<Post> getById(long id) {
        if (!repositoryMap.containsKey(id))
            return Optional.empty();
        return Optional.of(repositoryMap.get(id));
    }

    @Override
    public Post save(Post post) {
        if (post.getId() == 0) {
            post.setId(postCounter.incrementAndGet());
            repositoryMap.put(post.getId(), post);
        } else {
            if (repositoryMap.containsKey(post.getId())) {
                repositoryMap.put(post.getId(), post);
            } else {
                return null;
            }
        }
        return post;
    }

    @Override
    public void removeById(long id) {
        repositoryMap.remove(id);
    }
}
