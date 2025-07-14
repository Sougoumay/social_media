package sougoumay.fr.social_media.service;

import sougoumay.fr.social_media.entity.Post;

import java.util.List;

public class PostServiceImpl implements PostService {
    @Override
    public Post createPost(Long userId, String content) {
        return null;
    }

    @Override
    public List<Post> getUserPosts(Long userId) {
        return List.of();
    }

    @Override
    public List<Post> getFriendsPosts(Long userId) {
        return List.of();
    }

    @Override
    public void deletePost(Long postId, Long userId) {

    }
}
