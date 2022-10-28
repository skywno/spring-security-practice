package me.ezra.security.notice;

import lombok.RequiredArgsConstructor;
import me.ezra.security.note.NoteRegisterDto;
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
    public String postNotice(@ModelAttribute NoteRegisterDto noteRegisterDto) {
        noticeService.saveNotice(noteRegisterDto.getTitle(), noteRegisterDto.getContent());
        return "redirect:notice";
    }

    @DeleteMapping()
    public String deleteNotice(@RequestParam Long id) {
        noticeService.deleteNotice(id);
        return "redirect:notice";
    }
}
