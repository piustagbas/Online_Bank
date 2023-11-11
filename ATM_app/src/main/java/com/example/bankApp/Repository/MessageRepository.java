package com.example.bankApp.Repository;

import com.example.bankApp.Model.Conversation;
import com.example.bankApp.Model.Message;
import com.example.bankApp.Model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
        List<Message> findByConversation(Conversation conversation);
        List<Message> findBySender(Users sender);

}

