package com.example.bankApp.ServiceImp;

import com.example.bankApp.Model.Conversation;
import com.example.bankApp.Model.Users;
import com.example.bankApp.Repository.ConversationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class ConversationService {

    @Autowired
    private ConversationRepository conversationRepository;

    // Other dependencies...

    public Conversation createConversation(Set<Users> participants, String title) {
        Conversation conversation = new Conversation();
        conversation.setParticipants(participants);
        conversation.setTitle(title);

        return conversationRepository.save(conversation);
    }

    public Conversation getConversationByParticipants(Users participant1, Users participant2) {
        return conversationRepository.findByParticipantsContainingAndParticipantsContaining(participant1, participant2)
                .orElse(null);
    }
    public List<Conversation> getConversationsForUser(Users user) {
        return conversationRepository.findByParticipantsIn(Set.of(user));
    }
    public Conversation getConversationById(Long id) {
        return conversationRepository.findById(id).orElse(null);
    }

    public List<Conversation> getAllConversations() {
        return conversationRepository.findAll();
    }

    public void deleteConversation(Long id) {
        conversationRepository.deleteById(id);
    }
}

