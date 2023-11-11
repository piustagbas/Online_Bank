package com.example.bankApp.Service;

import com.example.bankApp.Exception.RecipientNotFoundException;
import com.example.bankApp.Model.Conversation;
import com.example.bankApp.Model.Message;
import com.example.bankApp.Model.Users;
import com.example.bankApp.dto.MessageDto;

import java.util.List;

public interface MessageService {
    Message createMessage(MessageDto messageDto) throws RecipientNotFoundException;

    List<Message> getMessagesByConversation(Conversation conversation);

    List<Message> getMessagesBySender(Users sender);
}

