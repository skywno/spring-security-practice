package me.ezra.security.note;

import lombok.RequiredArgsConstructor;
import me.ezra.security.Exception.UserNotFoundException;
import me.ezra.security.user.User;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class NoteService {
    private final NoteRepository noteRepository;

    @Transactional(readOnly = true)
    public List<Note> findByUser(User user) {
        if (user == null) {
            throw new UserNotFoundException();
        }
        if (user.isAdmin()) {
            return noteRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
        }
        return noteRepository.findByUserOrderByIdDesc(user);
    }

    public Note saveNote(User user, String title, String content) {
        if (user == null) {
            throw new UserNotFoundException();
        }
        return noteRepository.save(new Note(title, content, user));
    }

    public void deleteNote(User user, Long noteId) {
        if (user == null) {
            throw new UserNotFoundException();
        }
        noteRepository.findByIdAndUser(noteId, user)
                .ifPresent(note -> noteRepository.delete(note)
        );
    }
}
