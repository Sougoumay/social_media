package sougoumay.fr.social_media.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import sougoumay.fr.social_media.entity.User;
import sougoumay.fr.social_media.service.UserService;

import java.util.Set;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile/{username}")
    public String viewProfile(@PathVariable String username,
                              @AuthenticationPrincipal User currentUser,
                              Model model) {
        User user = userService.findByUsername(username);
        Set<User> currentUserFriends = userService.findByIdWithFriends(currentUser.getId()).getFriends();

        model.addAttribute("user", user);
        model.addAttribute("isOwnProfile", user.getId().equals(currentUser.getId()));
        model.addAttribute("isFriend", currentUserFriends.contains(user));
        return "profile";
    }

    @GetMapping("/search")
    public String searchUsers(@RequestParam String query,
                              @AuthenticationPrincipal User currentUser,
                              Model model) {
        model.addAttribute("users", userService.searchUsers(query, currentUser.getId()));
        model.addAttribute("query", query);
        return "search-results";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@AuthenticationPrincipal User currentUser,
                                @RequestParam String bio) {
        userService.updateProfile(currentUser.getId(), bio);
        return "redirect:/users/profile/" + currentUser.getUsername();
    }
}
