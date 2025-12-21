package sougoumay.fr.social_media.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import sougoumay.fr.social_media.entity.User;
import sougoumay.fr.social_media.service.MessageService;
import sougoumay.fr.social_media.service.UserService;

@Controller
@RequestMapping("/messages")
public class MessageController {

    private static final Logger log = LoggerFactory.getLogger(MessageController.class);

    private final MessageService messageService;
    private final UserService userService;

    public MessageController(MessageService messageService, UserService userService) {
        this.messageService = messageService;
        this.userService = userService;
    }

    @GetMapping
    public String messagesList(@AuthenticationPrincipal User currentUser, Model model) {
        log.info("User '{}' is viewing their conversation list", currentUser.getUsername());

        model.addAttribute("conversationPartners",
                messageService.getConversationPartners(currentUser.getId()));
        return "messages";
    }

    @GetMapping("/conversation/{userId}")
    public String viewConversation(@PathVariable Long userId,
                                   @AuthenticationPrincipal User currentUser,
                                   Model model) {
        log.info("User '{}' is opening conversation with user ID: {}", currentUser.getUsername(), userId);

        User otherUser = userService.findById(userId);
        model.addAttribute("otherUser", otherUser);
        model.addAttribute("messages",
                messageService.getConversation(currentUser.getId(), userId));
        return "conversation";
    }

    @PostMapping("/send")
    public String sendMessage(@RequestParam Long receiverId,
                              @RequestParam String content,
                              @AuthenticationPrincipal User currentUser) {
        log.info("User '{}' is sending a message to user ID: {}", currentUser.getUsername(), receiverId);

        messageService.sendMessage(currentUser.getId(), receiverId, content);
        return "redirect:/messages/conversation/" + receiverId;
    }
}
