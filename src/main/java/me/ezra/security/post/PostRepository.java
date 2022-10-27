package me.ezra.security.post;

import me.ezra.security.User.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findByUserAndStatus(User user, PostStatus status);

    Post findByIdAndUser(Long id, User user);

}
