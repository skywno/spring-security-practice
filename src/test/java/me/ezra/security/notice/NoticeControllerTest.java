package me.ezra.security.notice;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@Transactional
class NoticeControllerTest {

    private MockMvc mvc;

    @Autowired
    private NoticeRepository noticeRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp(@Autowired WebApplicationContext applicationContext) {
        this.mvc = MockMvcBuilders.webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .alwaysDo(print())
                .build();
    }

    @DisplayName("[GET] getNotice - 인증 없음")
    @Test
    void whenGettingNoticeWithoutAuthentication_thenRedirects() throws Exception {
        // When
        mvc.perform(get("/notice"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser
    @DisplayName("[GET] getNotice = 인증 있음")
    void whenGettingNoticeWithAuthentication_thenOk() throws Exception {
        // Given
        // When & Then
        mvc.perform(get("/notice"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("[POST] postNotice = 인증 없음")
    void whenPostingNoticeWithoutAuthentication_thenForbidden() throws Exception {
        // Given
        // When & Then
        mvc.perform(post("/notice")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("title", "제목")
                .param("content", "내용")
        ).andExpect(status().isForbidden());

    }

    @Test
    @WithMockUser(roles = {"USER"})
    @DisplayName("[POST] getNotpostNoticeice = 유저 인증 있음")
    void whenPostingNoticeWithAdminAuthorization_thenForbidden() throws Exception {
        // Given
        // When & Then
        mvc.perform(post("/notice").with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("title", "제목")
                .param("content", "내용")
        ).andExpect(status().isForbidden());

    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("[POST] postNotice = 어드민 인증 있음")
    void whenPostingNoticeWithAdminAuthorization_thenOk() throws Exception {
        // Given
        // When & Then
        mvc.perform(post("/notice").with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("title", "제목")
                .param("content", "내용")
        ).andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName("[DELETE] deleteNotice = 인증 없음 -> 로그인 페이지로 redirection")
    void whenDeletingNoticeWithoutAuthentication_thenForbidden() throws Exception {
        // Given
        Notice notice = noticeRepository.save(new Notice("제목", "내용"));
        // When & Then
        mvc.perform(delete("/notice")
                        .with(csrf())
                        .queryParam("id", notice.getId().toString())
                ).andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));

    }

    @Test
    @WithMockUser(roles = {"USER"})
    @DisplayName("[DELETE] deleteNotice = 유저 인증 있음")
    void whenDeletingNoticeWithAdminAuthorization_thenForbidden() throws Exception {
        // Given
        Notice notice = noticeRepository.save(new Notice("제목", "내용"));
        // When & Then
        mvc.perform(delete("/notice")
                .with(csrf())
                .queryParam("id", notice.getId().toString())
        ).andExpect(status().isForbidden());

    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("[DELETE] deleteNotice = 어드민 인증 있음 -> notice 페이지로 redirection")
    void whenDeletingNoticeWithAdminAuthorization_thenOk() throws Exception {
        // Given
        Notice notice = noticeRepository.save(new Notice("제목", "내용"));
        // When & Then
        mvc.perform(delete("/notice")
                        .with(csrf())
                        .queryParam("id", notice.getId().toString())
                ).andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("notice"));
    }


}