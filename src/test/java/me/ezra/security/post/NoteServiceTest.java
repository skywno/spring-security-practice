package me.ezra.security.note;

import me.ezra.security.user.User;
import me.ezra.security.user.UserRepository;
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
class NoteServiceTest {

    @Autowired
    private NoteService noteService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NoteRepository noteRepository;

    @Test
    void findByUser_유저가_게시물조회() {
        // given
        User user = userRepository.save(new User("username", "password", "ROLE_USER"));
        noteRepository.save(new Note("title1", "content1", user));
        noteRepository.save(new Note("title2", "content2", user));
        // when
        List<Note> notes = noteService.findByUser(user);
        // then
        assertEquals(2, notes.size());
        then(notes.size()).isEqualTo(2);
        Note note1 = notes.get(0);
        Note note2 = notes.get(1);

        // note1 = title2
        then(note1.getUser().getUsername()).isEqualTo("username");
        then(note1.getTitle()).isEqualTo("title2"); // 가장 늦게 insert된 데이터가 먼저 나와야합니다.
        then(note1.getContent()).isEqualTo("content2");
        // note2 = title1
        then(note2.getUser().getUsername()).isEqualTo("username");
        then(note2.getTitle()).isEqualTo("title1");
        then(note2.getContent()).isEqualTo("content1");
    }

}