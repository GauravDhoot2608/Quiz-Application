package com.quizapp.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Document("quiz_participants")
public class QuizParticipant {

    @Id
    private String id;

    @JsonIncludeProperties(value = {"id"})
    @NonNull
    @DocumentReference
    private Quiz quiz;

    @JsonIgnore
    @NonNull
    @DocumentReference
    private User user;

    @DocumentReference
    private List<QuizParticipantResponse> quizParticipantResponses = new ArrayList<>();

    @CreatedDate
    private LocalDateTime createdDate;
    @LastModifiedDate
    private LocalDateTime lastModifiedDate;
}
