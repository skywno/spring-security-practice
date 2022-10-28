package me.ezra.security.note;

import me.ezra.security.user.User;
import me.ezra.security.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@ActiveProfiles(profiles = "test")
@Transactional
class NoteControllerTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private NoteRepository noteRepository;
    private MockMvc mockMvc;
    private User user;
    private User admin;

    @BeforeEach
    public void setUp(@Autowired WebApplicationContext applicationContext) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .alwaysDo(print())
                .build();
        user = userRepository.save(new User("user123", "user", "ROLE_USER"));
        admin = userRepository.save(new User("admin123", "admin", "ROLE_ADMIN"));
    }

    @Test
    void getNote_인증없음() throws Exception {
        mockMvc.perform(get("/note"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    // WithUserDetails 로 테스트 하는 방법
    @WithUserDetails(
            value = "user123", // userDetailsService를 통해 가져올 수 있는 유저
            setupBefore = TestExecutionEvent.TEST_EXECUTION // 테스트 실행 직전에 유저를 가져온다.
    )
    void getNote_인증있음() throws Exception {
        mockMvc.perform(get("/note"))
                .andExpect(status().isOk())
                .andExpect(view().name("note/index"));
    }

    @Test
    void postNote_인증없음() throws Exception {
        mockMvc.perform(post("/note")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("title", "제목")
                        .param("content", "내용")
                ).andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    @WithUserDetails(
            value = "user123",
            setupBefore = TestExecutionEvent.TEST_EXECUTION
    )
    void postNote_유저인증있음() throws Exception {
        mockMvc.perform(post("/note")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("title", "제목")
                        .param("content", "내용")
                ).andExpect(redirectedUrl("note"))
                .andExpect(status().is3xxRedirection());
    }


    @Test
    @WithUserDetails(
            value = "admin123",
            setupBefore = TestExecutionEvent.TEST_EXECUTION
    )
    void postNote_어드민인증있음() throws Exception {
        mockMvc.perform(post("/note")
                .with(csrf())
                .with(user(admin))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("title", "제목")
                .param("content", "내용")
        ).andExpect(status().is4xxClientError());
    }

    @Test
    void deleteNote_인증없음() throws Exception {
        Note note = noteRepository.save(new Note("제목", "내용", user));
        mockMvc.perform(delete("/note?id=" + note.getId())
                        .with(csrf())
                ).andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    @WithUserDetails(
            value = "user123",
            setupBefore = TestExecutionEvent.TEST_EXECUTION
    )
    void deleteNote_유저인증있음() throws Exception {
        Note note = noteRepository.save(new Note("제목", "내용", user));
        mockMvc.perform(delete("/note")
                        .with(csrf())
                        .queryParam("id", note.getId().toString())
                ).andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("note"));
    }

    @Test
    @WithUserDetails(
            value = "admin123",
            setupBefore = TestExecutionEvent.TEST_EXECUTION
    )
    void deleteNote_어드민인증있음() throws Exception {
        Note note = noteRepository.save(new Note("제목", "내용", user));
        mockMvc.perform(delete("/note?id=" + note.getId())
                .with(csrf())
        ).andExpect(status().is4xxClientError());
    }
}