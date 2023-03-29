package com.quizapp.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@RequiredArgsConstructor
@AllArgsConstructor
@Document("quiz")
public class Quiz {

    @Id
    private String id;
    @NonNull
    private String title;
    @JsonIgnore
    @NonNull
    @DocumentReference
    private User author;
    private boolean published = false;
    private boolean active = true;

    @DocumentReference
    private List<QuizQuestion> quizQuestions = new ArrayList<>();

    @CreatedDate
    private LocalDateTime createdDate;
    @LastModifiedDate
    private LocalDateTime lastModifiedDate;
}
