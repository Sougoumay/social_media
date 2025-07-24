package sougoumay.fr.social_media.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sougoumay.fr.social_media.entity.Message;
import sougoumay.fr.social_media.entity.User;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    @Query("SELECT m FROM Message m WHERE (m.sender = ?1 AND m.receiver = ?2) OR (m.sender = ?2 AND m.receiver = ?1) ORDER BY m.createdAt")
    List<Message> findConversation(User user1, User user2);

    List<Message> findByReceiverAndIsReadFalse(User receiver);

//    @Query("SELECT DISTINCT CASE WHEN m.sender = ?1 THEN m.receiver ELSE m.sender END FROM Message m WHERE m.sender = ?1 OR m.receiver = ?1")
//    List<User> findConversationPartners(User user);

    @Query("SELECT m.receiver FROM Message m WHERE m.sender = :user")
    List<User> findReceiversBySender(@Param("user") User user);

    @Query("SELECT m.sender FROM Message m WHERE m.receiver = :user")
    List<User> findSendersByReceiver(@Param("user") User user);

}