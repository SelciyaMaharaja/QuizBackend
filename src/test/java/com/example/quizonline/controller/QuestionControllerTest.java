package com.example.quizonline.controller;

import com.example.quizonline.model.Question;
import com.example.quizonline.service.IQuestionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class QuestionControllerTest {

    @Mock
    private IQuestionService questionService;

    @InjectMocks
    private QuestionController questionController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ---------- create-new-question ----------
    @Test
    void testCreateQuestion_Success() {
        Question question = new Question();
        question.setId(1L);
        question.setQuestion("Sample question");

        when(questionService.createQuestion(question)).thenReturn(question);

        ResponseEntity<Question> response = questionController.createQuestion(question);

        assertEquals(201, response.getStatusCodeValue());
        assertEquals(question, response.getBody());
        verify(questionService, times(1)).createQuestion(question);
    }

    // ---------- all-questions ----------
    @Test
    void testGetAllQuestions() {
        List<Question> mockQuestions = List.of(new Question(), new Question());
        when(questionService.getAllQuestions()).thenReturn(mockQuestions);

        ResponseEntity<List<Question>> response = questionController.getAllQuestions();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().size());
        verify(questionService, times(1)).getAllQuestions();
    }

    // ---------- question/{id} ----------
    @Test
    void testGetQuestionById_Found() throws Exception {
        Question question = new Question();
        question.setId(1L);

        when(questionService.getQuestionById(1L)).thenReturn(Optional.of(question));

        ResponseEntity<Question> response = questionController.getQuestionById(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(question, response.getBody());
        verify(questionService, times(1)).getQuestionById(1L);
    }

    @Test
    void testGetQuestionById_NotFound() {
        when(questionService.getQuestionById(99L)).thenReturn(Optional.empty());

        assertThrows(ChangeSetPersister.NotFoundException.class,
                () -> questionController.getQuestionById(99L));

        verify(questionService, times(1)).getQuestionById(99L);
    }

    // ---------- update ----------
    @Test
    void testUpdateQuestion_Success() throws Exception {
        Question updated = new Question();
        updated.setId(1L);
        updated.setQuestion("Updated");

        when(questionService.updateQuestion(eq(1L), any(Question.class))).thenReturn(updated);

        ResponseEntity<Question> response = questionController.updateQuestion(1L, updated);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Updated", response.getBody().getQuestion());
        verify(questionService, times(1)).updateQuestion(1L, updated);
    }

    // ---------- delete ----------
    @Test
    void testDeleteQuestion() {
        doNothing().when(questionService).deleteQuestion(1L);

        ResponseEntity<Void> response = questionController.deleteQuestion(1L);

        assertEquals(204, response.getStatusCodeValue());
        assertNull(response.getBody());
        verify(questionService, times(1)).deleteQuestion(1L);
    }

    // ---------- subjects ----------
    @Test
    void testGetAllSubjects() {
        List<String> subjects = List.of("Math", "Science");
        when(questionService.getAllSubjects()).thenReturn(subjects);

        ResponseEntity<List<String>> response = questionController.getAllSubjects();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(subjects, response.getBody());
        verify(questionService, times(1)).getAllSubjects();
    }

    // ---------- quiz/fetch-questions-for-user ----------
    @Test
    void testGetQuestionsForUser() {
        List<Question> mockQuestions = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Question q = new Question();
            q.setId((long) i);
            mockQuestions.add(q);
        }

        when(questionService.getQuestionsForUser(3, "Math")).thenReturn(mockQuestions);

        ResponseEntity<List<Question>> response =
                questionController.getQuestionsForUser(3, "Math");

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().size() <= 3);
        verify(questionService, times(1)).getQuestionsForUser(3, "Math");
    }
}
