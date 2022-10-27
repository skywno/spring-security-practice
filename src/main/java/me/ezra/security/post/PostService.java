package me.ezra.security.post;

import lombok.RequiredArgsConstructor;
import me.ezra.security.Exception.UserNotFoundException;
import me.ezra.security.User.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {
    private final PostRepository postRepository;

    @Transactional(readOnly = true)
    public List<Post> findByUser(User user) {
        if (user == null) {
            throw new UserNotFoundException();
        }
        if (user.isAdmin()) {
            return postRepository.findByStatusOrderByIdDesc(PostStatus.Y);
        }
        return postRepository.findByUserAndStatusOrderByIdDesc(user, PostStatus.Y);
    }

    public Post savePost(User user, String title, String content) {
        if (user == null) {
            throw new UserNotFoundException();
        }
        return postRepository.save(new Post(title, content, user));
    }

    public void deletePost(User user, Long postId) {
        if (user == null) {
            throw new UserNotFoundException();
        }
        Post post = postRepository.findByIdAndUser(postId, user);
        postRepository.delete(post);
    }
}
