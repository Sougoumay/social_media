package sougoumay.fr.social_media.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sougoumay.fr.social_media.entity.FriendRequest;
import sougoumay.fr.social_media.entity.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {
    List<FriendRequest> findByReceiverAndStatus(User receiver, FriendRequest.RequestStatus status);
    List<FriendRequest> findBySenderAndStatus(User sender, FriendRequest.RequestStatus status);
    Optional<FriendRequest> findBySenderAndReceiver(User sender, User receiver);
    boolean existsBySenderAndReceiverAndStatus(User sender, User receiver, FriendRequest.RequestStatus status);
}