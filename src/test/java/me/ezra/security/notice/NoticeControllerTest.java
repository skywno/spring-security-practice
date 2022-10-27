package me.ezra.security.notice;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


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
    @DisplayName("[POST] postNotice = 유저 인증 있음")
    void whenPostingNoticeWithoutAuthentication_thenForbidden() throws Exception {
        // Given
        String content = objectMapper.writeValueAsString(new Notice("제목", "내용"));
        // When & Then
        mvc.perform(post("/notice")
                .content(content)
        ).andExpect(status().isForbidden());

    }

    @Test
    @WithMockUser(roles = {"USER"})
    @DisplayName("[POST] getNotpostNoticeice = 유저 인증 있음")
    void whenPostingNoticeWithAdminAuthorization_thenForbidden() throws Exception {
        // Given
        String content = objectMapper.writeValueAsString(new Notice("제목", "내용"));
        // When & Then
        mvc.perform(post("/notice").with(csrf())
                .content(content)
        ).andExpect(status().isForbidden());

    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("[POST] postNotice = 어드민 인증 있음")
    void whenPostingNoticeWithAdminAuthorization_thenOk() throws Exception {
        // Given
        String content = objectMapper.writeValueAsString(new Notice("제목", "내용"));
        // When & Then
        mvc.perform(post("/notice").with(csrf())
                .content(content)
        ).andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName("[DELETE] deleteNotice = 인증 없음 -> 로그인 페이지로 redireection")
    void whenDeletingNoticeWithoutAuthentication_thenForbidden() throws Exception {
        // Given
        Notice notice = noticeRepository.save(new Notice("제목", "내용"));
        // When & Then
        mvc.perform(delete("/notice/"+ notice.getId()).with(csrf())
        ).andExpect(status().is3xxRedirection());

    }

    @Test
    @WithMockUser(roles = {"USER"})
    @DisplayName("[DELETE] deleteNotice = 유저 인증 있음")
    void whenDeletingNoticeWithAdminAuthorization_thenForbidden() throws Exception {
        // Given
        Notice notice = noticeRepository.save(new Notice("제목", "내용"));
        // When & Then
        mvc.perform(delete("/notice/" + notice.getId()).with(csrf())
        ).andExpect(status().isForbidden());

    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("[DELETE] deleteNotice = 어드민 인증 있음")
    void whenDeletingNoticeWithAdminAuthorization_thenOk() throws Exception {
        // Given
        Notice notice = noticeRepository.save(new Notice("제목", "내용"));
        // When & Then
        mvc.perform(delete("/notice/" + notice.getId()).with(csrf())
        ).andExpect(status().is3xxRedirection());
    }


}