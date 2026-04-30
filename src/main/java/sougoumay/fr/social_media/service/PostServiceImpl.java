package sougoumay.fr.social_media.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sougoumay.fr.social_media.entity.Post;
import sougoumay.fr.social_media.entity.User;
import sougoumay.fr.social_media.repository.PostRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserService userService;

    private static final Logger log = LoggerFactory.getLogger(PostServiceImpl.class);

    public PostServiceImpl(PostRepository postRepository, UserService userService) {
        this.postRepository = postRepository;
        this.userService = userService;
    }

    @Override
    public Post createPost(Long userId, String content) {
        log.debug("Creating post for user ID: {} with content length: {} chars", userId, content.length());
        User user = userService.findById(userId);

        Post post = new Post();
        post.setAuthor(user);
        post.setContent(content);

        Post savedPost = postRepository.save(post);
        log.info("Post ID {} successfully created by user '{}'", savedPost.getId(), user.getUsername());
        return savedPost;
    }

    @Override
    public List<Post> getUserPosts(Long userId) {
        log.debug("Retrieving all posts for user ID: {}", userId);
        User user = userService.findById(userId);
        List<Post> posts = postRepository.findByAuthorOrderByCreatedAtDesc(user);
        log.info("Retrieved {} posts for user '{}'", posts.size(), user.getUsername());
        return posts;
    }

    @Override
    public List<Post> getFriendsPosts(Long userId) {
        log.debug("Building news feed for user ID: {}", userId);
        User user = userService.findById(userId);

        List<User> friends = new ArrayList<>(user.getFriends());
        friends.add(user);

        List<Post> feed = postRepository.findByAuthorsOrderByCreatedAtDesc(friends);
        log.info("News feed generated for user '{}': {} total posts found", user.getUsername(), feed.size());
        return feed;
    }

    @Override
    public void deletePost(Long postId, Long userId) {
        log.debug("Attempting to delete post ID: {} by user ID: {}", postId, userId);

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> {
                    log.warn("Delete failed: Post ID {} not found", postId);
                    return new RuntimeException("Post not found");
                });

        if (!post.getAuthor().getId().equals(userId)) {
            log.warn("Security Violation: User ID {} tried to delete post ID {} belonging to User ID {}",
                    userId, postId, post.getAuthor().getId());
            throw new RuntimeException("Unauthorized");
        }

        postRepository.delete(post);
        log.info("Post ID {} successfully deleted", postId);
    }
}