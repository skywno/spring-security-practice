package me.ezra.security.note;

import lombok.RequiredArgsConstructor;
import me.ezra.security.user.User;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/note")
public class NoteController {

    private final NoteService noteService;

    @GetMapping
    public String getNote(Authentication authentication, Model model) {
        User user = (User) authentication.getPrincipal();
        List<Note> notes = noteService.findByUser(user);
        model.addAttribute("notes", notes);
        return "note/index";
    }

    @PostMapping
    public String saveNote(Authentication authentication, @ModelAttribute NoteRegisterDto noteRegisterDto) {
        User user = (User) authentication.getPrincipal();
        noteService.saveNote(user, noteRegisterDto.getTitle(), noteRegisterDto.getContent());
        return "redirect:note";
    }

    @DeleteMapping
    public String deleteNote(Authentication authentication, @RequestParam Long id) {
        User user = (User) authentication.getPrincipal();
        noteService.deleteNote(user, id);
        return "redirect:note";
    }
}
