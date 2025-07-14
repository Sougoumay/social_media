package sougoumay.fr.social_media.service;

import sougoumay.fr.social_media.entity.FriendRequest;

import java.util.List;

public interface FriendService {
    FriendRequest sendFriendRequest(Long senderId, Long receiverId);
    void acceptFriendRequest(Long requestId, Long userId);
    void declineFriendRequest(Long requestId, Long userId);
    List<FriendRequest> getPendingRequests(Long userId);
    List<FriendRequest> getSentRequests(Long userId);
}
