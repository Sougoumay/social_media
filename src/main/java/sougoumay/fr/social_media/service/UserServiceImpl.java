package sougoumay.fr.social_media.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sougoumay.fr.social_media.controller.AuthController;
import sougoumay.fr.social_media.entity.User;
import sougoumay.fr.social_media.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final static Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Spring Security attempting to load user details for: {}", username);

        return userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("Authentication failed: username '{}' not found in database", username);
                    return new UsernameNotFoundException("User not found: " + username);
                });
    }

    @Override
    public User findByIdWithFriends(Long id) {
        log.info("Retrieving the user with id <{}> and his friends", id);

        Optional<User> optionalUser = userRepository.findByIdWithFriends(id);

        if(optionalUser.isEmpty()) {
            log.warn("User not found with Id : {}", id);
            throw new RuntimeException("User not found");
        }

        User user = optionalUser.get();
        log.info("User {} is found with the id : {}. He has {}", user.getUsername(), id, user.getFriends().size());
        return user;
    }

    @Override
    public User registerUser(String username, String email, String password) {
        log.debug("Registering user with username={}, email={}", username, email);

        if (userRepository.existsByUsername(username)) {
            log.warn("Registration failed - username already exists: {}", username);
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByEmail(email)) {
            log.warn("Registration failed - email already exists: {}", email);
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));

        User savedUser = userRepository.save(user);
        log.info("User successfully registered with id={}, username={}",
                savedUser.getId(), savedUser.getUsername());

        return savedUser;
    }

    @Override
    public User findById(Long id) {
        log.info("Retrieving the user with id : {}", id);

        Optional<User> optionalUser = userRepository.findById(id);

        if(optionalUser.isEmpty()) {
            log.warn("User not found with Id : {}", id);
            throw new RuntimeException("User not found");
        }

        User user = optionalUser.get();
        log.info("User {} is found with the id : {}", user.getUsername(), id);
        return user;
    }

    @Override
    public User findByUsername(String username) {
        log.debug("Searching for user with username: {}", username);

        return userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("Retrieval failed: user with username '{}' does not exist", username);
                    return new RuntimeException("User not found");
                });
    }

    @Override
    public List<User> searchUsers(String query, Long currentUserId) {
        log.debug("Searching users with query='{}' (excluding currentUserId={})", query, currentUserId);

        List<User> results = userRepository.searchUsers(query, currentUserId);

        log.info("Search completed: found {} users matching the query '{}'", results.size(), query);
        return results;
    }


    @Override
    public User updateProfile(Long userId, String bio) {
        log.info("Request to update profile for user ID: {}", userId);

        User user = findById(userId);

        log.debug("Updating bio for user {}: '{}'", user.getUsername(), bio);
        user.setBio(bio);

        User updatedUser = userRepository.save(user);
        log.info("Profile successfully updated for user: {}", updatedUser.getUsername());

        return updatedUser;
    }
}
