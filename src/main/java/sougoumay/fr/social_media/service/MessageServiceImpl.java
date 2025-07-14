package sougoumay.fr.social_media.service;

import sougoumay.fr.social_media.entity.Message;
import sougoumay.fr.social_media.entity.User;

import java.util.List;

public class MessageServiceImpl implements MessageService {
    @Override
    public Message sendMessage(Long senderId, Long receiverId, String content) {
        return null;
    }

    @Override
    public List<Message> getConversation(Long userId1, Long userId2) {
        return List.of();
    }

    @Override
    public List<User> getConversationPartners(Long userId) {
        return List.of();
    }

    @Override
    public long getUnreadCount(Long userId) {
        return 0;
    }
}
