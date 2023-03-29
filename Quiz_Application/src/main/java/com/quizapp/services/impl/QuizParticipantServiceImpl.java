package com.quizapp.services.impl;

import com.quizapp.entities.*;
import com.quizapp.enums.QuestionType;
import com.quizapp.exceptions.AuthenticationException;
import com.quizapp.exceptions.BadRequestException;
import com.quizapp.exceptions.ResourceNotFoundException;
import com.quizapp.payloads.*;
import com.quizapp.repositories.*;
import com.quizapp.services.QuizParticipantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class QuizParticipantServiceImpl implements QuizParticipantService {

    @Autowired
    private UserRepo userRepo;
    @Autowired
    private QuizRepo quizRepo;
    @Autowired
    private QuizQuestionRepo quizQuestionRepo;
    @Autowired
    private QuizParticipantRepo quizParticipantRepo;
    @Autowired
    private QuizParticipantResponseRepo quizParticipantResponseRepo;
    @Autowired
    private QuestionOptionRepo questionOptionRepo;

    @Override
    public QuizAnswersResponse participateInQuiz(String userId, String quizId, QuizAnswersRequest quizAnswersRequest) {
        User user = this.userRepo.findById(userId).orElseThrow(() -> new ResourceNotFoundException("user","user_id" , userId));
        Quiz quiz = this.quizRepo.findById(quizId).orElseThrow(() -> new ResourceNotFoundException("quiz","quiz_id",quizId));
        if (quiz.getAuthor().getId().equals(user.getId())) {
            throw new AuthenticationException("Invalid Access. Author of the quiz cannot participate in their own " + "quiz");
        }
        if (!quiz.isActive()) {
            throw new BadRequestException(Collections.singletonList("Inactive List"));
        }
        if (this.quizParticipantRepo.findByQuizEntityAndUserEntity(quiz, user).isPresent()) {
            throw new AuthenticationException("Invalid Access. You have already performed the quiz");
        }
        List<String> errors = validateBeforeParticipation(user, quiz, quizAnswersRequest);
        if (errors.isEmpty()) {
            double totalScore = 0.0;
            List<QuestionAnswerResponse> questionAnswerResponses = new ArrayList<>();
            List<QuizParticipantResponse> quizParticipantResponseEntities = new ArrayList<>();
            for (QuestionAnswer questionAnswer : quizAnswersRequest.getAnswers()) {
                Optional<QuizQuestion> quizQuestionOptional = this.quizQuestionRepo.findById(questionAnswer.getQuestionId());
                if (quizQuestionOptional.isPresent()) {
                    double individualScore = 0.0;
                    QuizQuestion quizQuestion = quizQuestionOptional.get();
                    List<String> correctChoices = quizQuestion.getQuestionOptionEntities().stream()
                            .filter(QuestionOption :: isCorrect)
                            .map(QuestionOption::getId)
                            .collect(Collectors.toList());
                    if (quizQuestion.getQuestionType() == QuestionType.SINGLE) {
                        String participantChoice = questionAnswer.getOptions().get(0);
                        individualScore += correctChoices.contains(participantChoice) ? 1.0 : -1.0;
                    } else {
                        int totalCorrectChoices = correctChoices.size();
                        int totalOptions = quizQuestion.getQuestionOptionEntities().size();
                        int totalIncorrectChoices = totalOptions - totalCorrectChoices;
                        for (String choice : questionAnswer.getOptions()) {
                            if (correctChoices.contains(choice)) {
                                individualScore += 1.0 / totalCorrectChoices;
                            } else {
                                individualScore -= 1.0 / totalIncorrectChoices;
                            }
                        }
                    }
                    individualScore = Math.round(individualScore * 100.0) / 100.0;
                    questionAnswerResponses.add(QuestionAnswerResponse.builder().questionId(quizQuestion.getId())
                            .score(individualScore)
                            .optionsSelected(questionAnswer.getOptions())
                            .scorePercentage(individualScore * 100.0)
                            .build());
                    QuizParticipantResponse quizParticipantResponse = new QuizParticipantResponse(quizQuestion);
                    quizParticipantResponse.setOptionsSelected(questionAnswer.getOptions());
                    quizParticipantResponseEntities.add(this.quizParticipantResponseRepo.save(quizParticipantResponse));
                    totalScore += individualScore;
                } else {
                    throw new BadRequestException(
                            Collections.singletonList("Question " + questionAnswer.getQuestionId() + " not present"));
                }
            }
            int totalQuestions = quiz.getQuizQuestions().size();
            double percentage = (totalScore * 100.0) / totalQuestions;
            QuizParticipant quizParticipant = new QuizParticipant(quiz, user);
            quizParticipant.setQuizParticipantResponses(quizParticipantResponseEntities);
            this.quizParticipantRepo.save(quizParticipant);
            return QuizAnswersResponse.builder().quizId(quizId).questionAnswers(questionAnswerResponses)
                    .totalScore(totalScore).totalScorePercentage(percentage).build();
        } else {
            throw new BadRequestException(errors);
        }
    }

    @Override
    public Map<String, Object> getAllQuizzesAttemptedByUser(String userId, int page, int size) {
        User user = this.userRepo.findById(userId).orElseThrow(() -> new ResourceNotFoundException("user", "user_id", userId));
        List<Quiz> inactiveQuizzes = this.quizRepo.findAllByActiveFalse();
        Pageable paging = PageRequest.of(page, size);
        Map<String, Object> responseMap = new HashMap<>();
        Page<QuizParticipant> quizParticipants = this.quizParticipantRepo.findAllByUserEntityAndQuizEntityNotIn(user, inactiveQuizzes, paging);
        List<PublishedQuiz> quizAttemptedList = new ArrayList<>();
        for (QuizParticipant quizParticipant : quizParticipants) {
            Quiz quiz = quizParticipant.getQuiz();
            List<QuestionWithoutSolution> questionWithoutSolutionList = new ArrayList<>();

            for (QuizParticipantResponse quizParticipantResponse : quizParticipant.getQuizParticipantResponses()) {
                QuizQuestion quizQuestion = quizParticipantResponse.getQuizQuestion();
                Map<String, String> optionsMap = quizQuestion.getQuestionOptionEntities().stream()
                        .filter(questionOptionEntity -> quizParticipantResponse.getOptionsSelected()
                                .contains(
                                        questionOptionEntity.getId()))
                        .collect(
                                Collectors.toMap(QuestionOption::getId,
                                        QuestionOption::getValue));

                questionWithoutSolutionList.add(QuestionWithoutSolution.builder().questionId(quizQuestion.getId())
                        .questionText(
                                quizQuestion.getQuestionText())
                        .questionType(
                                quizQuestion.getQuestionType())
                        .optionsIdAndText(optionsMap).build());

            }
            quizAttemptedList.add(PublishedQuiz.builder().title(quiz.getTitle()).id(quiz.getId())
                    .questions(questionWithoutSolutionList).build());
        }
        responseMap.put("currentPage", quizParticipants.getNumber());
        responseMap.put("totalItems", quizParticipants.getTotalElements());
        responseMap.put("totalPages", quizParticipants.getTotalPages());
        responseMap.put("quizzes", quizAttemptedList);
        return responseMap;
    }

    private List<String> validateBeforeParticipation(User userEntity, Quiz quizEntity, QuizAnswersRequest quizAnswersRequest) {
        List<String> errors = new ArrayList<>();
        if (!quizEntity.isPublished()) {
            errors.add("Quiz is Unpublished, Cannot Attempt");
            return errors;
        }
        if (quizAnswersRequest.getAnswers().size() != quizEntity.getQuizQuestions().size()) {
            errors.add("All Questions Are not Attempted");
            return errors;
        }
        boolean uniqueQuestion = quizAnswersRequest.getAnswers().stream().map(QuestionAnswer::getQuestionId)
                .collect(Collectors.toList()).stream().distinct()
                .count() == quizAnswersRequest.getAnswers().size();
        if (!uniqueQuestion) {
            errors.add("Duplicate Questions Found in Request, Please check");
            return errors;
        }
        for (QuestionAnswer questionAnswer : quizAnswersRequest.getAnswers()) {
            if (questionAnswer.getOptions().size() < 1) {
                errors.add("At least One Option must be selected");
            }
            Optional<QuizQuestion> quizQuestionEntity = quizEntity.getQuizQuestions().stream().filter(o -> o.getId().equals(questionAnswer.getQuestionId())).findAny();

            if (quizQuestionEntity.isEmpty()) {
                errors.add("This question does not belong to this quiz : " + questionAnswer.getQuestionId());
                return errors;
            }
            boolean unique =
                    questionAnswer.getOptions().stream().distinct().count() == questionAnswer.getOptions().size();
            if (!unique) {
                errors.add("Options Selected must be unique");
            }
            if (quizQuestionEntity.get().getQuestionType() == QuestionType.SINGLE && questionAnswer.getOptions()
                    .size() != 1) {
                errors.add("Only One Option must be selected for a Single Correct Question");
            }
            for (String optionId : questionAnswer.getOptions()) {
                if (quizQuestionEntity.get().getQuestionOptionEntities().stream()
                        .noneMatch(o -> o.getId().equals(optionId))) {
                    errors.add("Invalid Option ID : " + optionId);
                }
            }
        }
        return errors;
    }
}
