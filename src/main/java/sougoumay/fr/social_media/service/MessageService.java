package sougoumay.fr.social_media.service;

import sougoumay.fr.social_media.entity.Message;
import sougoumay.fr.social_media.entity.User;

import java.util.List;

public interface MessageService {
    Message sendMessage(Long senderId, Long receiverId, String content);
    List<Message> getConversation(Long userId1, Long userId2);
    List<User> getConversationPartners(Long userId);
    long getUnreadCount(Long userId);


}
