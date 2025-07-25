package sougoumay.fr.social_media.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import sougoumay.fr.social_media.entity.User;
import sougoumay.fr.social_media.service.FriendService;
import sougoumay.fr.social_media.service.UserService;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = FriendController.class)
public class FriendControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FriendService friendService;

    @MockBean
    private UserService userService;

    private User currentUser;
    private User friendUser;

    @BeforeEach
    void setup() {
        currentUser = new User();
        currentUser.setId(1L);
        currentUser.setUsername("currentUser");

        friendUser = new User();
        friendUser.setId(2L);
        friendUser.setUsername("friendUser");
    }

    @Test
    void testFriendsList() throws Exception {
        Set<User> friends = new HashSet<>();
        friends.add(friendUser);

        when(userService.findByIdWithFriends(currentUser.getId())).thenReturn(currentUser);
        currentUser.setFriends(friends);

        when(friendService.getPendingRequests(currentUser.getId())).thenReturn(Collections.emptyList());
        when(friendService.getSentRequests(currentUser.getId())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/friends")
                        .with(user(currentUser)))
                .andExpect(status().isOk())
                .andExpect(view().name("friends"))
                .andExpect(model().attributeExists("friends"))
                .andExpect(model().attributeExists("pendingRequests"))
                .andExpect(model().attributeExists("sentRequests"));

        verify(userService).findByIdWithFriends(currentUser.getId());
        verify(friendService).getPendingRequests(currentUser.getId());
        verify(friendService).getSentRequests(currentUser.getId());
    }

    @Test
    void testFriendsListEmpty() throws Exception {
        when(userService.findByIdWithFriends(currentUser.getId())).thenReturn(currentUser);
        currentUser.setFriends(Collections.emptySet());

        when(friendService.getPendingRequests(currentUser.getId())).thenReturn(Collections.emptyList());
        when(friendService.getSentRequests(currentUser.getId())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/friends")
                        .with(SecurityMockMvcRequestPostProcessors.user(currentUser)))
                .andExpect(status().isOk())
                .andExpect(view().name("friends"))
                .andExpect(model().attribute("friends", Collections.emptySet()))
                .andExpect(model().attribute("pendingRequests", Collections.emptyList()))
                .andExpect(model().attribute("sentRequests", Collections.emptyList()));

        verify(userService).findByIdWithFriends(currentUser.getId());
        verify(friendService).getPendingRequests(currentUser.getId());
        verify(friendService).getSentRequests(currentUser.getId());
    }

    @Test
    void testSendFriendRequest() throws Exception {
        Long receiverId = 3L;

        mockMvc.perform(post("/friends/request/{receiverId}", receiverId)
                        .with(user(currentUser))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/friends"));

        verify(friendService).sendFriendRequest(currentUser.getId(), receiverId);
    }

    @Test
    void testAcceptFriendRequest() throws Exception {
        Long requestId = 10L;

        mockMvc.perform(post("/friends/accept/{requestId}", requestId)
                        .with(user(currentUser))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/friends"));

        verify(friendService).acceptFriendRequest(requestId, currentUser.getId());
    }

    @Test
    void testDeclineFriendRequest() throws Exception {
        Long requestId = 20L;

        mockMvc.perform(post("/friends/decline/{requestId}", requestId)
                        .with(user(currentUser))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/friends"));

        verify(friendService).declineFriendRequest(requestId, currentUser.getId());
    }
}

