package ru.netology.repository;

import org.springframework.stereotype.Repository;
import ru.netology.exception.NotFoundException;
import ru.netology.model.Post;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class PostRepository {
  private static final String POST_NOT_FOUND = "Пост с id = %d не найден";
  private final ConcurrentHashMap<Long, Post> posts = new ConcurrentHashMap<>();
  private final AtomicLong postId = new AtomicLong();

  public List<Post> all() { return new ArrayList<>(posts.values()); }

  public Optional<Post> getById(long id) {
    return Optional.ofNullable(posts.get(id));
  }

  public Post save(Post post) {
    Post savingPost;
    long id = post.getId();
    if (post.getId() == 0) {
      postId.incrementAndGet();
      savingPost = new Post(postId.get(), post.getContent());
      posts.put(postId.get(), savingPost);
    } else if (posts.containsKey(id)) {
      savingPost = new Post(id, post.getContent());
      posts.replace(id, post);
    } else {
      throw new NotFoundException(String.format(POST_NOT_FOUND, id));
    }
    return savingPost;
  }

  public void removeById(long id) {
    if (posts.containsKey(id)) {
      posts.remove(id);
    } else {
      throw new NotFoundException(String.format(POST_NOT_FOUND, id));
    }
  }
}