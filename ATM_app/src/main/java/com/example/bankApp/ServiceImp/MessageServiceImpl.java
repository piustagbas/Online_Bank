package com.example.bankApp.ServiceImp;

import com.example.bankApp.Exception.RecipientNotFoundException;
import com.example.bankApp.Model.Mapper.MessageMapper;
import com.example.bankApp.Model.Conversation;
import com.example.bankApp.Model.Message;
import com.example.bankApp.Model.Users;
import com.example.bankApp.Repository.MessageRepository;
import com.example.bankApp.Repository.UserRepository;
import com.example.bankApp.Service.MessageService;
import com.example.bankApp.dto.MessageDto;
import com.google.firebase.messaging.FirebaseMessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final FcmService fcmService;
    private final UserRepository userRepository;
    private final ConversationService conversationService;
    private final MessageMapper messageMapper;
    @Autowired
    public MessageServiceImpl(MessageRepository messageRepository, FcmService fcmService, UserRepository userRepository, ConversationService conversationService, MessageMapper messageMapper) {
        this.messageRepository = messageRepository;
        this.fcmService = fcmService;
        this.userRepository = userRepository;
        this.conversationService = conversationService;
        this.messageMapper = messageMapper;
    }


    @Override
    public Message createMessage(MessageDto messageDto) throws RecipientNotFoundException {
        Users sender = userRepository.findById(messageDto.getSenderId())
                .orElseThrow(() -> new RecipientNotFoundException("Sender not found"));

        Users recipient = userRepository.findById(messageDto.getRecipientId())
                .orElseThrow(() -> new RecipientNotFoundException("Recipient not found"));

        // Check if a conversation exists between sender and recipient
        Conversation conversation = conversationService.getConversationByParticipants(sender, recipient);

        if (conversation == null) {
            // If not, create a new conversation
            conversation = conversationService.createConversation(Set.of(sender, recipient), "Private Conversation");
        }

        // Create and save the message
        Message message = messageMapper.convertToEntity(messageDto);
        message.setSender(sender);
        message.setConversation(conversation);

        Message savedMessage = messageRepository.save(message);

        // Send push notifications
        sendPushNotifications(conversation, savedMessage);

        return savedMessage;
    }


    @Override
    public List<Message> getMessagesByConversation(Conversation conversation) {
        return messageRepository.findByConversation(conversation);
    }
   @Override
    public List<Message> getMessagesBySender(Users sender) {
        return messageRepository.findBySender(sender);
    }

    private void sendPushNotifications(Conversation conversation, Message message) {
        List<Users> participants = conversation.getDistinctSenders();
        for (Users participant : participants) {
            String fcmToken = participant.getFcmToken();
            if (fcmToken != null && !fcmToken.isEmpty()) {
                try {
                    fcmService.sendNotification(fcmToken, "New Message", "You have a new message");
                } catch (FirebaseMessagingException e) {
                    // Handle exception
                }
            }
        }
    }
}

