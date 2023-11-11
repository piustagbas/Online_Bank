package com.example.bankApp.Repository;

import com.example.bankApp.Model.Conversation;
import com.example.bankApp.Model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    Optional<Conversation> findByParticipantsContainingAndParticipantsContaining(Users participant1, Users participant2);

    List<Conversation> findByParticipantsIn(Set<Users> participants);


}
