package sougoumay.fr.social_media.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import sougoumay.fr.social_media.entity.User;
import sougoumay.fr.social_media.service.MessageService;
import sougoumay.fr.social_media.service.PostService;


@Controller
public class HomeController {

    private final PostService postService;
    private final MessageService messageService;

    public HomeController(PostService postService, MessageService messageService) {
        this.postService = postService;
        this.messageService = messageService;
    }

    @GetMapping("/home")
    public String home(@AuthenticationPrincipal User currentUser, Model model) {
        model.addAttribute("posts", postService.getFriendsPosts(currentUser.getId()));
        model.addAttribute("unreadMessages", messageService.getUnreadCount(currentUser.getId()));
        return "home";
    }

    @PostMapping("/posts/create")
    public String createPost(@AuthenticationPrincipal User currentUser,
                             @RequestParam String content) {
        postService.createPost(currentUser.getId(), content);
        return "redirect:/home";
    }

    @PostMapping("/posts/{id}/delete")
    public String deletePost(@PathVariable Long id,
                             @AuthenticationPrincipal User currentUser) {
        postService.deletePost(id, currentUser.getId());
        return "redirect:/home";
    }
}
