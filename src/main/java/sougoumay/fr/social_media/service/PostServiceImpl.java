package sougoumay.fr.social_media.service;

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

    public PostServiceImpl(PostRepository postRepository, UserService userService) {
        this.postRepository = postRepository;
        this.userService = userService;
    }

    @Override
    public Post createPost(Long userId, String content) {
        User user = userService.findById(userId);

        Post post = new Post();
        post.setAuthor(user);
        post.setContent(content);

        return postRepository.save(post);
    }

    @Override
    public List<Post> getUserPosts(Long userId) {
        User user = userService.findById(userId);
        return postRepository.findByAuthorOrderByCreatedAtDesc(user);
    }

    @Override
    public List<Post> getFriendsPosts(Long userId) {
        User user = userService.findById(userId);
        List<User> friends = new ArrayList<>(user.getFriends());
        friends.add(user); // Include own posts

        return postRepository.findByAuthorsOrderByCreatedAtDesc(friends);
    }

    @Override
    public void deletePost(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        if (!post.getAuthor().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }

        postRepository.delete(post);
    }
}