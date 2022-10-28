package me.ezra.security.post;

import me.ezra.security.User.User;
import me.ezra.security.User.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.BDDAssertions.then;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles(profiles = "test")
@Transactional
class PostServiceTest {

    @Autowired
    private PostService postService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Test
    void findByUser_유저가_게시물조회() {
        // given
        User user = userRepository.save(new User("username", "password", "ROLE_USER"));
        postRepository.save(new Post("title1", "content1", user));
        postRepository.save(new Post("title2", "content2", user));
        // when
        List<Post> posts = postService.findByUser(user);
        // then
        assertEquals(2, posts.size());
        then(posts.size()).isEqualTo(2);
        Post post1 = posts.get(0);
        Post post2 = posts.get(1);

        // post1 = title2
        then(post1.getUser().getUsername()).isEqualTo("username");
        then(post1.getTitle()).isEqualTo("title2"); // 가장 늦게 insert된 데이터가 먼저 나와야합니다.
        then(post1.getContent()).isEqualTo("content2");
        // post2 = title1
        then(post2.getUser().getUsername()).isEqualTo("username");
        then(post2.getTitle()).isEqualTo("title1");
        then(post2.getContent()).isEqualTo("content1");
    }

}