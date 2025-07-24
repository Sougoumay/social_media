package sougoumay.fr.social_media.service;

import org.springframework.data.repository.query.Param;
import org.springframework.security.core.userdetails.UserDetailsService;
import sougoumay.fr.social_media.entity.User;

import java.util.List;

public interface UserService extends UserDetailsService {
    User registerUser(String username, String email, String password);
    User findById(Long id);
    User findByUsername(String username);
    List<User> searchUsers(String query, Long CurrentUserId);
    User updateProfile(Long userId, String bio);
    User findByIdWithFriends(Long id);

}
