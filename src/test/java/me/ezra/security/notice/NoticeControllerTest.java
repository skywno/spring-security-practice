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

    @DisplayName("[GET] getNotice - μΈμ¦ μμ")
    @Test
    void whenGettingNoticeWithoutAuthentication_thenRedirects() throws Exception {
        // When
        mvc.perform(get("/notice"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser
    @DisplayName("[GET] getNotice = μΈμ¦ μμ")
    void whenGettingNoticeWithAuthentication_thenOk() throws Exception {
        // Given
        // When & Then
        mvc.perform(get("/notice"))
                .andExpect(status().isOk())
                .andExpect(view().name("notice/index"));
    }

    @Test
    @DisplayName("[POST] postNotice = μΈμ¦ μμ")
    void whenPostingNoticeWithoutAuthentication_thenForbidden() throws Exception {
        // Given
        // When & Then
        mvc.perform(post("/notice")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("title", "μ λͺ©")
                .param("content", "λ΄μ©")
        ).andExpect(status().isForbidden());

    }

    @Test
    @WithMockUser(roles = {"USER"})
    @DisplayName("[POST] getNotpostNoticeice = μ μ  μΈμ¦ μμ")
    void whenPostingNoticeWithAdminAuthorization_thenForbidden() throws Exception {
        // Given
        // When & Then
        mvc.perform(post("/notice").with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("title", "μ λͺ©")
                .param("content", "λ΄μ©")
        ).andExpect(status().isForbidden());

    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("[POST] postNotice = μ΄λλ―Ό μΈμ¦ μμ")
    void whenPostingNoticeWithAdminAuthorization_thenOk() throws Exception {
        // Given
        // When & Then
        mvc.perform(post("/notice").with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("title", "μ λͺ©")
                .param("content", "λ΄μ©")
        ).andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName("[DELETE] deleteNotice = μΈμ¦ μμ -> λ‘κ·ΈμΈ νμ΄μ§λ‘ redirection")
    void whenDeletingNoticeWithoutAuthentication_thenForbidden() throws Exception {
        // Given
        Notice notice = noticeRepository.save(new Notice("μ λͺ©", "λ΄μ©"));
        // When & Then
        mvc.perform(delete("/notice")
                        .with(csrf())
                        .queryParam("id", notice.getId().toString())
                ).andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));

    }

    @Test
    @WithMockUser(roles = {"USER"})
    @DisplayName("[DELETE] deleteNotice = μ μ  μΈμ¦ μμ")
    void whenDeletingNoticeWithAdminAuthorization_thenForbidden() throws Exception {
        // Given
        Notice notice = noticeRepository.save(new Notice("μ λͺ©", "λ΄μ©"));
        // When & Then
        mvc.perform(delete("/notice")
                .with(csrf())
                .queryParam("id", notice.getId().toString())
        ).andExpect(status().isForbidden());

    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("[DELETE] deleteNotice = μ΄λλ―Ό μΈμ¦ μμ -> notice νμ΄μ§λ‘ redirection")
    void whenDeletingNoticeWithAdminAuthorization_thenOk() throws Exception {
        // Given
        Notice notice = noticeRepository.save(new Notice("μ λͺ©", "λ΄μ©"));
        // When & Then
        mvc.perform(delete("/notice")
                        .with(csrf())
                        .queryParam("id", notice.getId().toString())
                ).andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("notice"));
    }


}