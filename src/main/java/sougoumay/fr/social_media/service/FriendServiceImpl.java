package sougoumay.fr.social_media.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sougoumay.fr.social_media.controller.FriendController;
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

    private static final Logger log = LoggerFactory.getLogger(FriendService.class);

    public FriendServiceImpl(FriendRequestRepository friendRequestRepository,
                             UserRepository userRepository,
                             UserService userService) {
        this.friendRequestRepository = friendRequestRepository;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @Override
    public FriendRequest sendFriendRequest(Long senderId, Long receiverId) {

        log.debug("Processing friend request from sender {} to receiver {}", senderId, receiverId);

        if (senderId.equals(receiverId)) {
            log.warn("Validation failed: User {} tried to send a request to themselves", senderId);
            throw new RuntimeException("Cannot send friend request to yourself");
        }

        User sender = userService.findById(senderId);
        User receiver = userService.findById(receiverId);

        if (sender.getFriends().contains(receiver)) {
            log.warn("Validation failed: Users {} and {} are already friends", sender.getUsername(), receiver.getUsername());
            throw new RuntimeException("Already friends");
        }

        if (friendRequestRepository.existsBySenderAndReceiverAndStatus(
                sender, receiver, FriendRequest.RequestStatus.PENDING)) {
            log.warn("Validation failed: Pending request already exists between {} and {}", sender.getUsername(), receiver.getUsername());
            throw new RuntimeException("Friend request already sent");
        }

        FriendRequest request = new FriendRequest();
        request.setSender(sender);
        request.setReceiver(receiver);

        FriendRequest saved = friendRequestRepository.save(request);
        log.info("Friend request successfully created (ID: {}) between {} and {}", saved.getId(), sender.getUsername(), receiver.getUsername());
        return saved;
    }

    @Override
    public void acceptFriendRequest(Long requestId, Long userId) {
        log.debug("Attempting to accept request ID: {} by user ID: {}", requestId, userId);

        FriendRequest request = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> {
                    log.warn("Request ID {} not found", requestId);
                    return new  RuntimeException("Friend request not found");
                });

        if (!request.getReceiver().getId().equals(userId)) {
            log.warn("Security alert: User {} tried to accept request ID {} which belongs to user {}", userId, requestId, request.getReceiver().getId());
            throw new RuntimeException("Unauthorized");
        }

        if (request.getStatus() != FriendRequest.RequestStatus.PENDING) {
            log.warn("Request {} cannot be accepted (current status: {})", requestId, request.getStatus());
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

        log.info("Friendship established between '{}' and '{}'", sender.getUsername(), receiver.getUsername());
    }

    @Override
    public void declineFriendRequest(Long requestId, Long userId) {
        log.debug("User {} is attempting to decline request ID: {}", userId, requestId);
        FriendRequest request = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> {
                    log.warn("Decline failed: Friend request ID {} does not exist", requestId);
                    return new RuntimeException("Friend request not found");
                });

        if (!request.getReceiver().getId().equals(userId)) {
            log.warn("Security Alert: User {} tried to decline a request intended for user {}",
                    userId, request.getReceiver().getId());
            throw new RuntimeException("Unauthorized");
        }

        request.setStatus(FriendRequest.RequestStatus.DECLINED);
        request.setUpdatedAt(LocalDateTime.now());
        friendRequestRepository.save(request);

        log.info("Friend request {} was successfully declined by user '{}'",
                requestId, request.getReceiver().getUsername());
    }

    @Override
    public List<FriendRequest> getPendingRequests(Long userId) {
        log.debug("Fetching pending requests for user ID: {}", userId);
        User user = userService.findById(userId);
        List<FriendRequest> requests = friendRequestRepository.findByReceiverAndStatus(user, FriendRequest.RequestStatus.PENDING);

        log.info("Found {} pending requests for user {}", requests.size(), user.getUsername());
        return requests;
    }

    @Override
    public List<FriendRequest> getSentRequests(Long userId) {
        log.debug("Fetching all pending requests sent by user ID: {}", userId);

        User user = userService.findById(userId);

        List<FriendRequest> sentRequests = friendRequestRepository.findBySenderAndStatus(user, FriendRequest.RequestStatus.PENDING);
        log.info("User '{}' has {} outgoing pending friend requests", user.getUsername(), sentRequests.size());

        return sentRequests;
    }
}