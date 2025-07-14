package sougoumay.fr.social_media.service;

import sougoumay.fr.social_media.entity.Post;

import java.util.List;

public interface PostService {
    Post createPost(Long userId, String content);
    List<Post> getUserPosts(Long userId);
    List<Post> getFriendsPosts(Long userId);
    void deletePost(Long postId, Long userId);

}
