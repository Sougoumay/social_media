package sougoumay.fr.social_media.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import sougoumay.fr.social_media.entity.User;
import sougoumay.fr.social_media.service.UserService;

import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/users")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile/{username}")
    public String viewProfile(@PathVariable String username,
                              @AuthenticationPrincipal User currentUser,
                              Model model) {
        log.info("User '{}' is viewing the profile of '{}'", currentUser.getUsername(), username);
        User user = userService.findByUsername(username);
        log.debug("Checking friendship status between '{}' and '{}'", currentUser.getUsername(), username);

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
        log.info("User '{}' is searching for users with query: '{}'", currentUser.getUsername(), query);

        List<User> results = userService.searchUsers(query, currentUser.getId());
        log.debug("Search for '{}' returned {} results", query, results.size());

        model.addAttribute("users", results);
        model.addAttribute("query", query);
        return "search-results";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@AuthenticationPrincipal User currentUser,
                                @RequestParam String bio) {
        log.info("User '{}' is updating their profile bio", currentUser.getUsername());

        userService.updateProfile(currentUser.getId(), bio);

        log.info("Profile update successful for user '{}'", currentUser.getUsername());
        return "redirect:/users/profile/" + currentUser.getUsername();
    }
}
