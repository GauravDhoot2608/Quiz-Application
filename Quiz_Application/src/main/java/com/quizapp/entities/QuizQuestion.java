package com.quizapp.entities;

import com.quizapp.enums.QuestionType;
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
@Document("quiz_questions")
public class QuizQuestion {

    @Id
    private String id;
    @NonNull
    private QuestionType questionType;
    @NonNull
    private String questionText;
    @DocumentReference
    private List<QuestionOption> questionOptionEntities = new ArrayList<>();
    @CreatedDate
    private LocalDateTime createdDate;
    @LastModifiedDate
    private LocalDateTime lastModifiedDate;
}
