package me.ezra.security.post;

import lombok.RequiredArgsConstructor;
import me.ezra.security.User.User;
import me.ezra.security.User.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;

    public List<Post> findByUserName(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("유저가 없습니다.");
        }
        return postRepository.findByUserAndStatus(user, PostStatus.Y);
    }

    public Post savePost(String username, String title, String content) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("유저가 없습니다.");
        }
        return postRepository.save(new Post(title, content, user));
    }

    public void deletePost(String username, Long id) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("유저가 없습니다.");
        }
        Post post = postRepository.findByIdAndUser(id, user);
        postRepository.delete(post);
    }
}
