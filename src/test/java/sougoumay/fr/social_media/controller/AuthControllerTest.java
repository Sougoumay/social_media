package sougoumay.fr.social_media.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import sougoumay.fr.social_media.config.EncoderConfig;
import sougoumay.fr.social_media.config.SecurityConfig;
import sougoumay.fr.social_media.service.UserService;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AuthController.class)
@Import({SecurityConfig.class, EncoderConfig.class})
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean  // Changement ici
    private UserService userService;

    @Test
    void testGetRootRedirectsToLogin() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void testGetLoginPage() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login-view"));
    }

    @Test
    void testGetRegisterPage() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register-view"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    void testPostRegisterValidForm() throws Exception {
        mockMvc.perform(post("/register")
                .with(csrf())
                        .param("username", "user1")
                        .param("email", "user1@example.com")
                        .param("password", "password123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?registered"));

        verify(userService).registerUser("user1", "user1@example.com", "password123");
    }

    @Test
    void testPostRegisterWithValidationErrors() throws Exception {
        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("username", "")  // username empty to trigger validation error
                        .param("email", "not-an-email")
                        .param("password", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("register-view"));
    }

    @Test
    void testPostRegisterServiceThrowsException() throws Exception {
        doThrow(new RuntimeException("Username already exists"))
                .when(userService).registerUser(anyString(), anyString(), anyString());

        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("username", "user1")
                        .param("email", "user1@example.com")
                        .param("password", "password123"))
                .andExpect(status().isOk())
                .andExpect(view().name("register-view"))
                .andExpect(model().attributeExists("error"))
                .andExpect(model().attribute("error", "Username already exists"));
    }
}
