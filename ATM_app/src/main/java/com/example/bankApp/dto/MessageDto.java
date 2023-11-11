package com.example.bankApp.dto;

import lombok.Data;

@Data
public class MessageDto {
    private Long senderId;
    private Long recipientId;
    private String content;
}
