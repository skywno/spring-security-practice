package me.ezra.security.note;

import me.ezra.security.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NoteRepository extends JpaRepository<Note, Long> {

    List<Note> findByUserOrderByIdDesc(User user);

    Optional<Note> findByIdAndUser(Long id, User user);

}
