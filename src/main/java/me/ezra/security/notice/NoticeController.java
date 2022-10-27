package me.ezra.security.notice;

import lombok.RequiredArgsConstructor;
import me.ezra.security.post.PostDto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/notice")
public class NoticeController {
    private final NoticeService noticeService;

    @GetMapping
    public String getNotice(Model model) {
        List<Notice> notices = noticeService.findAll();
        model.addAttribute("notices", notices);
        return "notice/index";
    }

    @PostMapping
    public String postNotice(@ModelAttribute PostDto postDto) {
        noticeService.saveNotice(postDto.getTitle(), postDto.getContent());
        return "redirect:notice";
    }

    @DeleteMapping("/{id}")
    public String deleteNotice(@PathVariable Long id) {
        noticeService.deleteNotice(id);
        return "redirect:notice";
    }
}
