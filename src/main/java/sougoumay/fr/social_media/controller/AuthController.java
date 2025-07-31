package sougoumay.fr.social_media.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import sougoumay.fr.social_media.service.UserService;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String index() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login-view";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("user", new RegisterForm());
        return "register-view";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("user") RegisterForm form,
                           BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "register-view";
        }

        try {
            userService.registerUser(form.getUsername(), form.getEmail(), form.getPassword());
            return "redirect:/login?registered";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "register-view";
        }
    }

    public static class RegisterForm {
        private String username;
        @Email
        private String email;
        private String password;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
}
