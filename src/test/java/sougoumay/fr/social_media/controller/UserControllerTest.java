package sougoumay.fr.social_media.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import sougoumay.fr.social_media.entity.User;
import sougoumay.fr.social_media.service.UserService;

import java.util.*;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    User u1, u2;

    @BeforeEach
    void setup() {
        u1 = new User();
        u1.setId(1L);
        u1.setUsername("bob");

        u2 = new User();
        u2.setId(2L);
        u2.setUsername("alice");

        Set<User> friends = new HashSet<>();
        friends.add(u2);
        when(userService.findByUsername("bob")).thenReturn(u1);
        when(userService.findByUsername("alice")).thenReturn(u2);
        when(userService.findByIdWithFriends(1L)).thenReturn(u1);
        when(userService.findByIdWithFriends(2L)).thenReturn(u2);

        u1.setFriends(friends); // bob a alice en ami
        u2.setFriends(new HashSet<>()); // alice n'a pas d'amis
    }

    @Test
//    @WithMockUser(username="bob")
    void testViewOwnProfile() throws Exception {
        when(userService.findByUsername("bob")).thenReturn(u1);
        mockMvc.perform(get("/users/profile/bob")
                        .with(SecurityMockMvcRequestPostProcessors.user(u1)))
                .andExpect(status().isOk())
                .andExpect(view().name("profile"))
                .andExpect(model().attribute("user", u1))
                .andExpect(model().attribute("isOwnProfile", true))
                .andExpect(model().attribute("isFriend", false));
    }

    @Test
//    @WithMockUser(username="bob")
    void testViewFriendProfile() throws Exception {
        when(userService.findByUsername("alice")).thenReturn(u2);
        mockMvc.perform(get("/users/profile/alice")
                        .with(SecurityMockMvcRequestPostProcessors.user(u1)))
                .andExpect(status().isOk())
                .andExpect(view().name("profile"))
                .andExpect(model().attribute("user", u2))
                .andExpect(model().attribute("isOwnProfile", false))
                .andExpect(model().attribute("isFriend", true));
    }

    @Test
//    @WithMockUser(username="bob")
    void testViewNonFriendProfile() throws Exception {
        User stranger = new User(); stranger.setId(3L); stranger.setUsername("stranger");
        when(userService.findByUsername("stranger")).thenReturn(stranger);
        mockMvc.perform(get("/users/profile/stranger")
                        .with(SecurityMockMvcRequestPostProcessors.user(u1)))
                .andExpect(status().isOk())
                .andExpect(view().name("profile"))
                .andExpect(model().attribute("user", stranger))
                .andExpect(model().attribute("isOwnProfile", false))
                .andExpect(model().attribute("isFriend", false));
    }

    @Test
//    @WithMockUser(username="bob")
    void testSearchUsersResult() throws Exception {
        List<User> list = Arrays.asList(u2);
        when(userService.searchUsers("ali", 1L)).thenReturn(list);

        mockMvc.perform(get("/users/search").param("query", "ali")
                        .with(SecurityMockMvcRequestPostProcessors.user(u1)))
                .andExpect(status().isOk())
                .andExpect(view().name("search-results"))
                .andExpect(model().attribute("users", list))
                .andExpect(model().attribute("query", "ali"));
    }

    @Test
//    @WithMockUser(username="bob")
    void testSearchUsersNoResult() throws Exception {
        when(userService.searchUsers("nobody", 1L)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/users/search").param("query", "nobody")
                        .with(SecurityMockMvcRequestPostProcessors.user(u1)))
                .andExpect(status().isOk())
                .andExpect(view().name("search-results"))
                .andExpect(model().attribute("users", Collections.emptyList()))
                .andExpect(model().attribute("query", "nobody"));
    }

    @Test
//    @WithMockUser(username="bob")
    void testUpdateProfileSuccess() throws Exception {
        mockMvc.perform(post("/users/profile/update").param("bio", "new bio")
                        .with(SecurityMockMvcRequestPostProcessors.user(u1))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users/profile/bob"));

        verify(userService).updateProfile(1L, "new bio");
    }
}
