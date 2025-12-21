package sougoumay.fr.social_media.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(MessageServiceImpl.class);
    private final MessageRepository messageRepository;
    private final UserService userService;

    public MessageServiceImpl(MessageRepository messageRepository, UserService userService) {
        this.messageRepository = messageRepository;
        this.userService = userService;
    }

    @Override
    public Message sendMessage(Long senderId, Long receiverId, String content) {
        log.debug("Attempting to send message from {} to {}", senderId, receiverId);

        User sender = userService.findById(senderId);
        User receiver = userService.findById(receiverId);

        if (!sender.getFriends().contains(receiver)) {
            log.warn("Blocked message: User '{}' and '{}' are not friends", sender.getUsername(), receiver.getUsername());
            throw new RuntimeException("Can only send messages to friends");
        }

        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(content);

        Message saved = messageRepository.save(message);
        log.info("Message sent successfully (ID: {}) from '{}' to '{}'",
                saved.getId(), sender.getUsername(), receiver.getUsername());
        return saved;
    }

    @Override
    public List<Message> getConversation(Long userId1, Long userId2) {
        log.debug("Fetching conversation history between users {} and {}", userId1, userId2);
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

        long readCount = messages.stream()
                .filter(m -> m.getReceiver().getId().equals(userId1) && !m.isRead())
                .peek(m -> m.setRead(true))
                .count();

        if (readCount > 0) {
            log.info("Marked {} messages as read for user '{}' in conversation with '{}'",
                    readCount, user1.getUsername(), user2.getUsername());
        }

        log.debug("Retrieved {} total messages for this conversation", messages.size());
        return messages;
    }

    @Override
    public List<User> getConversationPartners(Long userId) {
        log.debug("Retrieving all unique conversation partners for user ID: {}", userId);

        User user = userService.findById(userId);

        List<User> receivers = messageRepository.findReceiversBySender(user);
        List<User> senders = messageRepository.findSendersByReceiver(user);
        Set<User> partners = new HashSet<>();
        partners.addAll(receivers);
        partners.addAll(senders);

        log.info("User '{}' has conversations with {} unique partners", user.getUsername(), partners.size());
        return new ArrayList<>(partners);
    }

    @Override
    public long getUnreadCount(Long userId) {
        log.debug("Checking unread message count for user ID: {}", userId);

        User user = userService.findById(userId);
        long count = messageRepository.findByReceiverAndIsReadFalse(user).size();

        if (count > 0) {
            log.debug("User ID {} has {} unread messages", userId, count);
        }
        return count;
    }
}