package me.ezra.security.post;

import lombok.RequiredArgsConstructor;
import me.ezra.security.User.User;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final PostService postService;

    @GetMapping("/post")
    public String getPostByAdmin(Authentication authentication, Model model) {
        User user = (User) authentication.getPrincipal();
        List<Post> posts = postService.findByUser(user);
        model.addAttribute("posts", posts);
        return "admin/index";
    }
}
