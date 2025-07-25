package sougoumay.fr.social_media.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import sougoumay.fr.social_media.entity.Post;
import sougoumay.fr.social_media.entity.User;
import sougoumay.fr.social_media.service.MessageService;
import sougoumay.fr.social_media.service.PostService;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HomeController.class)
public class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PostService postService;

    @MockBean
    private MessageService messageService;

    private User currentUser;

    @BeforeEach
    void setUp() {
        currentUser = new User();
        currentUser.setId(1L);
        currentUser.setUsername("bob");
    }

    @Test
    void testGetHome() throws Exception {
        Post post1 = mock(Post.class);
        Post post2 = mock(Post.class);
        when(post1.getAuthor()).thenReturn(currentUser);
        when(post2.getAuthor()).thenReturn(currentUser);
        List<Post> posts = Arrays.asList(post1, post2);
        when(postService.getFriendsPosts(currentUser.getId())).thenReturn(posts);
        when(messageService.getUnreadCount(currentUser.getId())).thenReturn(5L);

        mockMvc.perform(get("/home").with(user(currentUser)))
                .andExpect(status().isOk())
                .andExpect(view().name("home-view"))
                .andExpect(model().attribute("posts", posts))
                .andExpect(model().attribute("unreadMessages", 5L));

        verify(postService).getFriendsPosts(currentUser.getId());
        verify(messageService).getUnreadCount(currentUser.getId());
    }

    @Test
    void testCreatePost() throws Exception {
        String content = "Hello world!";

        mockMvc.perform(post("/posts/create")
                        .with(user(currentUser))
                        .with(csrf())
                        .param("content", content))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"));

        verify(postService).createPost(currentUser.getId(), content);
    }

    @Test
    void testDeletePost() throws Exception {
        Long postId = 42L;

        mockMvc.perform(post("/posts/{id}/delete", postId)
                        .with(user(currentUser))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"));

        verify(postService).deletePost(postId, currentUser.getId());
    }
}
