package com.example.bankApp.ServiceImp;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FcmService {
    public void sendNotification(String token, String title, String body) throws FirebaseMessagingException {
        Message message = Message.builder()
                .putData("Update of your account", title)
                .putData("Please update your account before it is too late", body)
                .setToken(token)
                .build();

        FirebaseMessaging.getInstance().send(message);
    }
}

