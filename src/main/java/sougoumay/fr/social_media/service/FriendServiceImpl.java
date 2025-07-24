package sougoumay.fr.social_media.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sougoumay.fr.social_media.entity.FriendRequest;
import sougoumay.fr.social_media.entity.User;
import sougoumay.fr.social_media.repository.FriendRequestRepository;
import sougoumay.fr.social_media.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class FriendServiceImpl implements FriendService {
    private final FriendRequestRepository friendRequestRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    public FriendServiceImpl(FriendRequestRepository friendRequestRepository,
                             UserRepository userRepository,
                             UserService userService) {
        this.friendRequestRepository = friendRequestRepository;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @Override
    public FriendRequest sendFriendRequest(Long senderId, Long receiverId) {
        if (senderId.equals(receiverId)) {
            throw new RuntimeException("Cannot send friend request to yourself");
        }

        User sender = userService.findById(senderId);
        User receiver = userService.findById(receiverId);

        if (sender.getFriends().contains(receiver)) {
            throw new RuntimeException("Already friends");
        }

        if (friendRequestRepository.existsBySenderAndReceiverAndStatus(
                sender, receiver, FriendRequest.RequestStatus.PENDING)) {
            throw new RuntimeException("Friend request already sent");
        }

        FriendRequest request = new FriendRequest();
        request.setSender(sender);
        request.setReceiver(receiver);

        return friendRequestRepository.save(request);
    }

    @Override
    public void acceptFriendRequest(Long requestId, Long userId) {
        FriendRequest request = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Friend request not found"));

        if (!request.getReceiver().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }

        if (request.getStatus() != FriendRequest.RequestStatus.PENDING) {
            throw new RuntimeException("Request already processed");
        }

        request.setStatus(FriendRequest.RequestStatus.ACCEPTED);
        request.setUpdatedAt(LocalDateTime.now());
        friendRequestRepository.save(request);

        User sender = request.getSender();
        User receiver = request.getReceiver();

        sender.getFriends().add(receiver);
        receiver.getFriends().add(sender);

        userRepository.save(sender);
        userRepository.save(receiver);
    }

    @Override
    public void declineFriendRequest(Long requestId, Long userId) {
        FriendRequest request = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Friend request not found"));

        if (!request.getReceiver().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }

        request.setStatus(FriendRequest.RequestStatus.DECLINED);
        request.setUpdatedAt(LocalDateTime.now());
        friendRequestRepository.save(request);
    }

    @Override
    public List<FriendRequest> getPendingRequests(Long userId) {
        User user = userService.findById(userId);
        return friendRequestRepository.findByReceiverAndStatus(user, FriendRequest.RequestStatus.PENDING);
    }

    @Override
    public List<FriendRequest> getSentRequests(Long userId) {
        User user = userService.findById(userId);
        return friendRequestRepository.findBySenderAndStatus(user, FriendRequest.RequestStatus.PENDING);
    }
}