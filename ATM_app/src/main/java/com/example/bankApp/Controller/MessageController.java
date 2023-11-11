package com.example.bankApp.Controller;

import com.example.bankApp.Exception.RecipientNotFoundException;
import com.example.bankApp.Exception.UserNotFoundException;
import com.example.bankApp.Model.Conversation;
import com.example.bankApp.Model.Message;
import com.example.bankApp.Model.Users;
import com.example.bankApp.Service.MessageService;
import com.example.bankApp.ServiceImp.ConversationService;
import com.example.bankApp.ServiceImp.UserServiceImpl;
import com.example.bankApp.dto.MessageDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
//@RequiredArgsConstructor

public class MessageController {

//    @Autowired
    private final MessageService messageService;
    private final ConversationService conversationService;
    private final UserServiceImpl userService;
    @Autowired
    public MessageController(MessageService messageService, ConversationService conversationService, UserServiceImpl userService) {
        this.messageService = messageService;
        this.conversationService = conversationService;
        this.userService = userService;
    }

    @PostMapping("/send")
    public ResponseEntity<Message> sendMessage(@RequestBody MessageDto messageDto) {
        try {
            Message sentMessage = messageService.createMessage(messageDto);
            return ResponseEntity.ok(sentMessage);
        } catch (RecipientNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Conversation> getConversationById(@PathVariable Long id) {
        Conversation conversation = conversationService.getConversationById(id);
        if (conversation != null) {
            return ResponseEntity.ok(conversation);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<Conversation>> getAllConversations() {
        List<Conversation> conversations = conversationService.getAllConversations();
        return ResponseEntity.ok(conversations);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteConversation(@PathVariable Long id) {
        conversationService.deleteConversation(id);
        return ResponseEntity.ok("Conversation deleted successfully.");
    }

    @GetMapping("/messages/{senderId}")
    public ResponseEntity<List<Message>> getMessagesBySender(@PathVariable Long senderId) {
        try {
            Users sender = userService.getUserById(senderId);
            if (sender != null) {
                List<Message> messages = messageService.getMessagesBySender(sender);
                return ResponseEntity.ok(messages);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
        } catch (NumberFormatException e) {
            // Handle invalid senderId (not a valid Long)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

}

