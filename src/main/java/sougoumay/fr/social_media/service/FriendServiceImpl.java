package sougoumay.fr.social_media.service;

import sougoumay.fr.social_media.entity.FriendRequest;

import java.util.List;

public class FriendServiceImpl implements FriendService {
    @Override
    public FriendRequest sendFriendRequest(Long senderId, Long receiverId) {
        return null;
    }

    @Override
    public void acceptFriendRequest(Long requestId, Long userId) {

    }

    @Override
    public void declineFriendRequest(Long requestId, Long userId) {

    }

    @Override
    public List<FriendRequest> getPendingRequests(Long userId) {
        return List.of();
    }

    @Override
    public List<FriendRequest> getSentRequests(Long userId) {
        return List.of();
    }
}
