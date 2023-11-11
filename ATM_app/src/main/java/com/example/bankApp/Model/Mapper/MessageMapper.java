package com.example.bankApp.Model.Mapper;

import com.example.bankApp.Model.Message;
import com.example.bankApp.dto.MessageDto;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class MessageMapper {

    private final ModelMapper modelMapper;

    public MessageMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public Message convertToEntity(MessageDto messageDto) {
        return modelMapper.map(messageDto, Message.class);
    }

    public MessageDto convertToDto(Message message) {
        return modelMapper.map(message, MessageDto.class);
    }
}

