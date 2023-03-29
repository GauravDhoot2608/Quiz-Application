package com.quizapp.services.impl;

import com.quizapp.entities.*;
import com.quizapp.enums.QuestionType;
import com.quizapp.exceptions.AuthenticationException;
import com.quizapp.exceptions.BadRequestException;
import com.quizapp.exceptions.ResourceNotFoundException;
import com.quizapp.payloads.*;
import com.quizapp.repositories.*;
import com.quizapp.services.QuizAuthorService;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class QuizAuthorServiceImpl implements QuizAuthorService {

    @Autowired
    private UserRepo userRepo;
    @Autowired
    private QuizRepo quizRepo;
    @Autowired
    private QuizQuestionRepo quizQuestionRepo;
    @Autowired
    private QuestionOptionRepo questionOptionRepo;
    @Autowired
    private QuizParticipantRepo quizParticipantRepo;

    @Override
    public String createQuiz(String userId, QuizRequest quizRequest) {
        User userEntity = this.userRepo.findById(userId).orElseThrow(() -> new ResourceNotFoundException("user" ,"user_id" , userId));
        List<String> errors = validateQuizRequestForCreation(quizRequest);
        if (errors.isEmpty()) {
            Quiz quizEntity = new Quiz(quizRequest.getTitle(), userEntity);
            Quiz savedAfterName = this.quizRepo.save(quizEntity);
            if (quizRequest.getQuestions() != null && !quizRequest.getQuestions().isEmpty()) {
                List<QuizQuestion> quizQuestionEntities = new ArrayList<>();
                for (QuestionRequest questionRequest : quizRequest.getQuestions()) {
                    QuizQuestion quizQuestionEntity = new QuizQuestion(questionRequest.getType(), questionRequest.getText());
                    List<QuestionOption> questionOptionEntities = new ArrayList<>();
                    for (QuestionOptionRequest questionOptionRequest : questionRequest.getOptions()) {
                        QuestionOption questionOptionEntity = new QuestionOption(questionOptionRequest.getValue());
                        questionOptionEntity.setCorrect(questionOptionRequest.getIsCorrect());
                        questionOptionEntities.add(this.questionOptionRepo.save(questionOptionEntity));
                    }
                    quizQuestionEntity.setQuestionOptionEntities(questionOptionEntities);
                    QuizQuestion saved = this.quizQuestionRepo.save(quizQuestionEntity);
                    quizQuestionEntities.add(saved);
                }
                savedAfterName.setQuizQuestions(quizQuestionEntities);
                this.quizRepo.save(savedAfterName);
            }
            return savedAfterName.getId();
        } else {
            throw new BadRequestException(errors);
        }
    }

    @Override
    public String updateQuizTitle(String userId, String quizId, String title) {
        User userEntity = this.userRepo.findById(userId).orElseThrow(() -> new ResourceNotFoundException("user","user_id", userId));
        Quiz quizEntity = this.quizRepo.findById(quizId).orElseThrow(() -> new ResourceNotFoundException("quiz","quiz_id", quizId));
        if (!quizEntity.getAuthor().getId().equals(userEntity.getId())) {
            throw new AuthenticationException("Invalid Access. Only Author can perform this Action");
        }
        if (!quizEntity.isActive()) {
            throw new BadRequestException(Collections.singletonList("Inactive List"));
        }
        if (quizEntity.isPublished()) {
            throw new BadRequestException(Collections.singletonList("Quiz is Already Published"));
        }

        if (!Strings.isBlank(title)) {
            quizEntity.setTitle(title);
        }
        return this.quizRepo.save(quizEntity).getId();
    }

    @Override
    public void publishQuiz(String userId, String quizId) {
        User userEntity = this.userRepo.findById(userId).orElseThrow(() -> new ResourceNotFoundException("user","user_id", userId));
        Quiz quizEntity = this.quizRepo.findById(quizId).orElseThrow(() -> new ResourceNotFoundException("quiz","quiz_id", quizId));
        if (!quizEntity.getAuthor().getId().equals(userEntity.getId())) {
            throw new AuthenticationException("Invalid Access. Only Author can perform this Action");
        }
        if (!quizEntity.isActive()) {
            throw new BadRequestException(Collections.singletonList("Inactive List"));
        }
        if (quizEntity.isPublished()) {
            throw new BadRequestException(Collections.singletonList("Quiz Already Published"));
        }
        quizEntity.setPublished(true);
        this.quizRepo.save(quizEntity);
    }

    @Override
    public Map<String, Object> getAllQuizzesForAuthor(String userId, int page, int size) {
        User userEntity = this.userRepo.findById(userId).orElseThrow(() -> new ResourceNotFoundException("user","user_id", userId));
        Pageable paging = PageRequest.of(page, size);
        Page<Quiz> quizEntities = this.quizRepo.findAllByAuthorAndActiveTrue(userEntity, paging);
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("currentPage", quizEntities.getNumber());
        responseMap.put("totalItems", quizEntities.getTotalElements());
        responseMap.put("totalPages", quizEntities.getTotalPages());
        responseMap.put("quizzes", quizEntities.getContent());
        return responseMap;
    }

    @Override
    public Quiz getQuizForAuthor(String userId, String quizId) {
        User userEntity = this.userRepo.findById(userId).orElseThrow(() -> new ResourceNotFoundException("user","user_id", userId));
        Quiz quizEntity = this.quizRepo.findById(quizId).orElseThrow(() -> new ResourceNotFoundException("quiz","quiz_id", quizId));
        if (!quizEntity.getAuthor().getId().equals(userEntity.getId())) {
            throw new AuthenticationException("Invalid Access. Only Author can perform this Action");
        }
        if (!quizEntity.isActive()) {
            throw new BadRequestException(Collections.singletonList("Inactive List"));
        }
        return quizEntity;
    }

    @Override
    public Map<String, Object> getAllResponsesOnThisQuiz(String userId, String quizId, int page, int size) {
        User userEntity = this.userRepo.findById(userId).orElseThrow(() -> new ResourceNotFoundException("user","user_id", userId));
        Quiz quizEntity = this.quizRepo.findById(quizId).orElseThrow(() -> new ResourceNotFoundException("quiz","quiz_id", quizId));
        if (!quizEntity.getAuthor().getId().equals(userEntity.getId())) {
            throw new AuthenticationException("Invalid Access. Only Author can perform this Action");
        }
        if (!quizEntity.isActive()) {
            throw new BadRequestException(Collections.singletonList("Inactive List"));
        }
        Pageable paging = PageRequest.of(page, size);
        Page<QuizParticipant> quizParticipantEntities = this.quizParticipantRepo.findAllByQuizEntity(quizEntity,
                paging);
        List<PublishedQuiz> quizAttemptedList = new ArrayList<>();
        for (QuizParticipant quizParticipantEntity : quizParticipantEntities) {
            List<QuestionWithoutSolution> questionWithoutSolutionList = new ArrayList<>();

            for (QuizParticipantResponse quizParticipantResponseEntity : quizParticipantEntity.getQuizParticipantResponses()) {

                QuizQuestion quizQuestionEntity = quizParticipantResponseEntity.getQuizQuestion();
                Map<String, String> optionsMap = quizQuestionEntity.getQuestionOptionEntities().stream()
                        .filter(questionOptionEntity -> quizParticipantResponseEntity.getOptionsSelected()
                                .contains(
                                        questionOptionEntity.getId()))
                        .collect(
                                Collectors.toMap(QuestionOption::getId, QuestionOption::getValue));
                questionWithoutSolutionList.add(QuestionWithoutSolution.builder().questionId(quizQuestionEntity.getId())
                        .questionText(
                                quizQuestionEntity.getQuestionText())
                        .questionType(
                                quizQuestionEntity.getQuestionType())
                        .optionsIdAndText(optionsMap).build());

            }
            quizAttemptedList.add(PublishedQuiz.builder().title(quizEntity.getTitle()).id(quizEntity.getId())
                    .questions(questionWithoutSolutionList).build());
        }
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("currentPage", quizParticipantEntities.getNumber());
        responseMap.put("totalItems", quizParticipantEntities.getTotalElements());
        responseMap.put("totalPages", quizParticipantEntities.getTotalPages());
        responseMap.put("quizzes", quizAttemptedList);
        return responseMap;
    }

    @Override
    public void deactivateQuiz(String userId, String quizId) {
        User userEntity = this.userRepo.findById(userId).orElseThrow(() -> new ResourceNotFoundException("user","user_id", userId));
        Quiz quizEntity = this.quizRepo.findById(quizId).orElseThrow(() -> new ResourceNotFoundException("quiz","quiz_id", quizId));
        if (!quizEntity.getAuthor().getId().equals(userEntity.getId())) {
            throw new AuthenticationException("Invalid Access. Only Author can perform this Action");
        }
        if (!quizEntity.isActive()) {
            throw new BadRequestException(Collections.singletonList("Inactive List"));
        }
        quizEntity.setActive(false);
        this.quizRepo.save(quizEntity);
    }

    @Override
    public QuizQuestion addQuestionForQuiz(String userId, String quizId, QuestionRequest questionRequest) {
        User userEntity = this.userRepo.findById(userId).orElseThrow(() -> new ResourceNotFoundException("user","user_id", userId));
        Quiz quizEntity = this.quizRepo.findById(quizId).orElseThrow(() -> new ResourceNotFoundException("quiz","quiz_id", quizId));
        if (!quizEntity.getAuthor().getId().equals(userEntity.getId())) {
            throw new AuthenticationException("Invalid Access. Only Author can perform this Action");
        }
        if (!quizEntity.isActive()) {
            throw new BadRequestException(Collections.singletonList("Inactive List"));
        }
        if (quizEntity.isPublished()) {
            throw new BadRequestException(Collections.singletonList("Quiz is Already Published"));
        }
        if (quizEntity.getQuizQuestions().size() < 10) {

            List<String> errors = validateNewQuestionCreation(questionRequest, "");
            if (!errors.isEmpty()) {
                throw new BadRequestException(errors);
            }
            QuizQuestion quizQuestionEntity = new QuizQuestion(questionRequest.getType(),
                    questionRequest.getText());
            List<QuestionOption> questionOptionEntities = new ArrayList<>();
            for (QuestionOptionRequest questionOptionRequest : questionRequest.getOptions()) {
                QuestionOption questionOptionEntity = new QuestionOption(questionOptionRequest.getValue());
                questionOptionEntity.setCorrect(questionOptionRequest.getIsCorrect());
                questionOptionEntities.add(this.questionOptionRepo.save(questionOptionEntity));
            }
            quizQuestionEntity.setQuestionOptionEntities(questionOptionEntities);
            QuizQuestion saved = this.quizQuestionRepo.save(quizQuestionEntity);
            quizEntity.getQuizQuestions().add(saved);
            this.quizRepo.save(quizEntity);
            return saved;
        } else {
            throw new BadRequestException(Collections.singletonList("Maximum 10 questions are allowed in a quiz"));
        }
    }

    @Override
    public QuizQuestion updateQuestionForQuiz(String userId, String quizId, QuizQuestion quizQuestionEntity) {
        User userEntity = this.userRepo.findById(userId).orElseThrow(() -> new ResourceNotFoundException("user","user_id", userId));
        Quiz quizEntity = this.quizRepo.findById(quizId).orElseThrow(() -> new ResourceNotFoundException("quiz","quiz_id", quizId));
        if (!quizEntity.getAuthor().getId().equals(userEntity.getId())) {
            throw new AuthenticationException("Invalid Access. Only Author can perform this Action");
        }
        if (!quizEntity.isActive()) {
            throw new BadRequestException(Collections.singletonList("Inactive List"));
        }
        if (quizEntity.isPublished()) {
            throw new BadRequestException(Collections.singletonList("Quiz is Already Published"));
        }
        Optional<QuizQuestion> previousQuizEntity = quizEntity.getQuizQuestions().stream()
                        .filter(o -> o.getId().equals(quizQuestionEntity.getId())).findAny();
        if (previousQuizEntity.isEmpty()) {
            throw new BadRequestException(Collections.singletonList("Question Does not Belong to Quiz"));
        }
        List<String> errors = validateExistingQuestion(quizQuestionEntity);
        if (!errors.isEmpty()) {
            throw new BadRequestException(errors);
        }
        List<QuestionOption> questionOptionEntities = new ArrayList<>();
        for (QuestionOption questionOptionEntity : quizQuestionEntity.getQuestionOptionEntities()) {
            questionOptionEntities.add(this.questionOptionRepo.save(questionOptionEntity));
        }
        quizQuestionEntity.setQuestionOptionEntities(questionOptionEntities);
        return this.quizQuestionRepo.save(quizQuestionEntity);
    }

    @Override
    public void removeQuestionFromQuizQuestion(String userId, String quizId, String questionId) {
        User userEntity = this.userRepo.findById(userId).orElseThrow(() -> new ResourceNotFoundException("user","user_id", userId));
        Quiz quizEntity = this.quizRepo.findById(quizId).orElseThrow(() -> new ResourceNotFoundException("quiz","quiz_id", quizId));
        if (!quizEntity.getAuthor().getId().equals(userEntity.getId())) {
            throw new AuthenticationException("Invalid Access. Only Author can perform this Action");
        }
        if (!quizEntity.isActive()) {
            throw new BadRequestException(Collections.singletonList("Inactive List"));
        }
        if (quizEntity.isPublished()) {
            throw new BadRequestException(Collections.singletonList("Quiz is Already Published"));
        }
        Optional<QuizQuestion> previousQuestionEntity = quizEntity.getQuizQuestions().stream()
                .filter(o -> o.getId().equals(questionId))
                .findAny();
        if (previousQuestionEntity.isEmpty()) {
            throw new BadRequestException(Collections.singletonList("Question does not exist in this Quiz"));
        }
        quizEntity.getQuizQuestions().remove(previousQuestionEntity.get());
        this.questionOptionRepo.deleteAll(previousQuestionEntity.get().getQuestionOptionEntities());
        this.quizQuestionRepo.delete(previousQuestionEntity.get());
        this.quizRepo.save(quizEntity);
    }

    private List<String> validateQuizRequestForCreation(QuizRequest quizRequest) {
        List<String> errors = new ArrayList<>();
        if (quizRequest.getQuestions().isEmpty() || quizRequest.getQuestions().size() > 10) {
            errors.add("Quiz must have minimum 1 question and maximum 10 questions");
            return errors;
        }
        List<QuestionRequest> questions = quizRequest.getQuestions();
        for (int i = 0; i < questions.size(); i++) {
            QuestionRequest questionRequest = questions.get(i);
            errors.addAll(validateNewQuestionCreation(questionRequest, String.valueOf(i + 1)));
        }
        return errors;
    }

    List<String> validateNewQuestionCreation(QuestionRequest questionRequest, String i) {
        List<String> errors = new ArrayList<>();
        if (questionRequest.getType() == QuestionType.SINGLE) {
            if (questionRequest.getOptions().size() != 2) {
                errors.add("No. of Options must be 2 for a Single Correct Question");
            }
            boolean allTrue = questionRequest.getOptions().stream().allMatch(QuestionOptionRequest::getIsCorrect);
            if (allTrue) {
                errors.add("At most One option should be marked as correct. Check Question " + i);
            }
        } else {
            if (questionRequest.getOptions().size() > 5 || questionRequest.getOptions().size() < 2) {
                errors.add("No. of Options must be minimum 2 and maximum 5 in a Question. Check Question " + i);
            }
        }
        boolean anyTrue = questionRequest.getOptions().stream().anyMatch(QuestionOptionRequest::getIsCorrect);
        if (!anyTrue) {
            errors.add("At least One option should be marked as correct. Check Question " + i);
        }
        return errors;
    }

    List<String> validateExistingQuestion(QuizQuestion quizQuestionEntity) {
        List<String> errors = new ArrayList<>();
        if (quizQuestionEntity.getQuestionType() == QuestionType.SINGLE) {
            if (quizQuestionEntity.getQuestionOptionEntities().size() != 2) {
                errors.add("No. of Options must be 2 for a Single Correct Question");
            }
            boolean allTrue = quizQuestionEntity.getQuestionOptionEntities().stream()
                    .allMatch(QuestionOption ::isCorrect);
            if (allTrue) {
                errors.add("At most One option should be marked as correct.");
            }
        } else {
            if (quizQuestionEntity.getQuestionOptionEntities()
                    .size() > 5 || quizQuestionEntity.getQuestionOptionEntities().size() < 2) {
                errors.add("No. of Options must be minimum 2 and maximum 5 in a Question");
            }
        }
        boolean anyTrue = quizQuestionEntity.getQuestionOptionEntities().stream()
                .anyMatch(QuestionOption ::isCorrect);
        if (!anyTrue) {
            errors.add("At least One option should be marked as correct");
        }
        return errors;
    }
}
