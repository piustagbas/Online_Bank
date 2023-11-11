package com.example.bankApp.Model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class Conversation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "conversation_id")
    private Long id;

    @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Message> messages = new ArrayList<>();

    @ManyToMany
    @JsonManagedReference
    @JoinTable(
            name = "conversation_participants",
            joinColumns = @JoinColumn(name = "conversation_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<Users> participants = new HashSet<>();

    private String title;


    public void addMessage(Message message) {
        this.messages.add(message);
        message.setConversation(this);
    }

    public void removeMessage(Message message) {
        this.messages.remove(message);
        message.setConversation(null);
    }

    public void addParticipant(Users user) {
        this.participants.add(user);
    }

    public void removeParticipant(Users user) {
        this.participants.remove(user);
    }
    @Transient
    public List<Users> getDistinctSenders() {
        return messages.stream()
                .map(Message::getSender)
                .distinct()
                .collect(Collectors.toList());
    }

}

