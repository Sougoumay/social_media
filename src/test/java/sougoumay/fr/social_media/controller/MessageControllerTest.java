package sougoumay.fr.social_media.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import sougoumay.fr.social_media.entity.Message;
import sougoumay.fr.social_media.entity.User;
import sougoumay.fr.social_media.service.MessageService;
import sougoumay.fr.social_media.service.UserService;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MessageController.class)
public class MessageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MessageService messageService;

    @MockBean
    private UserService userService;

    private User currentUser;

    @BeforeEach
    void setUp() {
        currentUser = new User();
        currentUser.setId(1L);
        currentUser.setUsername("bob");
    }

    @Test
    void testMessagesList() throws Exception {
        List<User> partners = Arrays.asList(new User(), new User());
        when(messageService.getConversationPartners(currentUser.getId())).thenReturn(partners);

        mockMvc.perform(get("/messages").with(user(currentUser)))
                .andExpect(status().isOk())
                .andExpect(view().name("messages"))
                .andExpect(model().attribute("conversationPartners", partners));

        verify(messageService).getConversationPartners(currentUser.getId());
    }

    @Test
    void testViewConversation() throws Exception {
        Long otherUserId = 2L;
        User otherUser = new User();
        otherUser.setId(otherUserId);
        otherUser.setUsername("alice");

        Message message1 = mock(Message.class);
        Message message2 = mock(Message.class);

        when(message1.getSender()).thenReturn(currentUser);
        when(message2.getSender()).thenReturn(currentUser);
        List<Message> messages = Arrays.asList(message1, message2);

        when(userService.findById(otherUserId)).thenReturn(otherUser);
        when(messageService.getConversation(currentUser.getId(), otherUserId)).thenReturn(messages);

        mockMvc.perform(get("/messages/conversation/{userId}", otherUserId)
                        .with(user(currentUser)))
                .andExpect(status().isOk())
                .andExpect(view().name("conversation"))
                .andExpect(model().attribute("otherUser", otherUser))
                .andExpect(model().attribute("messages", messages));

        verify(userService).findById(otherUserId);
        verify(messageService).getConversation(currentUser.getId(), otherUserId);
    }

    @Test
    void testSendMessage() throws Exception {
        Long receiverId = 2L;
        String content = "Bonjour";

        mockMvc.perform(post("/messages/send")
                        .with(user(currentUser))
                        .with(csrf())
                        .param("receiverId", receiverId.toString())
                        .param("content", content))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/messages/conversation/" + receiverId));

        verify(messageService).sendMessage(currentUser.getId(), receiverId, content);
    }
}
