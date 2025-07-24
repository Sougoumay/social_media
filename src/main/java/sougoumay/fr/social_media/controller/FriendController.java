package sougoumay.fr.social_media.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import sougoumay.fr.social_media.entity.User;
import sougoumay.fr.social_media.service.FriendService;
import sougoumay.fr.social_media.service.UserService;

@Controller
@RequestMapping("/friends")
public class FriendController {

    private final FriendService friendService;
    private final UserService userService;

    public FriendController(FriendService friendService, UserService userService) {
        this.friendService = friendService;
        this.userService = userService;
    }

    @GetMapping
    public String friendsList(@AuthenticationPrincipal User currentUser, Model model) {
        User userWithFriends = userService.findByIdWithFriends(currentUser.getId());
        model.addAttribute("friends", userWithFriends.getFriends());
        model.addAttribute("pendingRequests", friendService.getPendingRequests(currentUser.getId()));
        model.addAttribute("sentRequests", friendService.getSentRequests(currentUser.getId()));
        return "friends";
    }

    @PostMapping("/request/{receiverId}")
    public String sendFriendRequest(@PathVariable Long receiverId,
                                    @AuthenticationPrincipal User currentUser) {
        friendService.sendFriendRequest(currentUser.getId(), receiverId);
        return "redirect:/friends";
    }

    @PostMapping("/accept/{requestId}")
    public String acceptRequest(@PathVariable Long requestId,
                                @AuthenticationPrincipal User currentUser) {
        friendService.acceptFriendRequest(requestId, currentUser.getId());
        return "redirect:/friends";
    }

    @PostMapping("/decline/{requestId}")
    public String declineRequest(@PathVariable Long requestId,
                                 @AuthenticationPrincipal User currentUser) {
        friendService.declineFriendRequest(requestId, currentUser.getId());
        return "redirect:/friends";
    }
}