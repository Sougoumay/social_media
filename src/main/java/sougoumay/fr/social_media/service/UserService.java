package sougoumay.fr.social_media.service;

import sougoumay.fr.social_media.entity.User;

import java.util.List;

public interface UserService {
    User registerUser(String username, String email, String password);
    User findById(Long id);
    User findByUsername(String username);
    List<User> searchUsers(String query, Long CurrentUserId);
    User updateProfile(Long userId, String bio);

}
