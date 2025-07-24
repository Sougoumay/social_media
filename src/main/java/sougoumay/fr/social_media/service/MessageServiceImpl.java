package sougoumay.fr.social_media.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sougoumay.fr.social_media.entity.Message;
import sougoumay.fr.social_media.entity.User;
import sougoumay.fr.social_media.repository.MessageRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final UserService userService;

    public MessageServiceImpl(MessageRepository messageRepository, UserService userService) {
        this.messageRepository = messageRepository;
        this.userService = userService;
    }

    @Override
    public Message sendMessage(Long senderId, Long receiverId, String content) {
        User sender = userService.findById(senderId);
        User receiver = userService.findById(receiverId);

        if (!sender.getFriends().contains(receiver)) {
            throw new RuntimeException("Can only send messages to friends");
        }

        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(content);

        return messageRepository.save(message);
    }

    @Override
    public List<Message> getConversation(Long userId1, Long userId2) {
        User user1 = userService.findById(userId1);
        User user2 = userService.findById(userId2);

        List<Message> messages = messageRepository.findConversation(user1, user2);

        // Mark messages as read
        messages.forEach(m -> {
            if (m.getReceiver().getId().equals(userId1) && !m.isRead()) {
                m.setRead(true);
                messageRepository.save(m);
            }
        });

        return messages;
    }

    @Override
    public List<User> getConversationPartners(Long userId) {
        User user = userService.findById(userId);
//        return messageRepository.findConversationPartners(user);
        List<User> receivers = messageRepository.findReceiversBySender(user);
        List<User> senders = messageRepository.findSendersByReceiver(user);
        Set<User> partners = new HashSet<>();
        partners.addAll(receivers);
        partners.addAll(senders);
        return new ArrayList<>(partners);
    }

    @Override
    public long getUnreadCount(Long userId) {
        User user = userService.findById(userId);
        return messageRepository.findByReceiverAndIsReadFalse(user).size();
    }
}