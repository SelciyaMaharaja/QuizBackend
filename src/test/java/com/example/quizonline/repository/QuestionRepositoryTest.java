package com.example.quizonline.repository;

import com.example.quizonline.model.Question;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class QuestionRepositoryTest {

    @Mock
    private QuestionRepository questionRepository;

    private Question question1;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        question1 = new Question();
        question1.setId(1L);
        question1.setSubject("Math");
        question1.setQuestion("What is 2+2?");
    }

    @Test
    void testFindDistinctSubject() {
        List<String> subjects = Arrays.asList("Math", "Science");
        when(questionRepository.findDistinctSubject()).thenReturn(subjects);

        List<String> result = questionRepository.findDistinctSubject();

        assertEquals(2, result.size());
        assertTrue(result.contains("Math"));
        assertTrue(result.contains("Science"));
        verify(questionRepository, times(1)).findDistinctSubject();
    }

    @Test
    void testFindBySubject() {
        Page<Question> page = new PageImpl<>(Arrays.asList(question1));
        when(questionRepository.findBySubject(eq("Math"), any(PageRequest.class))).thenReturn(page);

        Page<Question> resultPage = questionRepository.findBySubject("Math", PageRequest.of(0, 10));

        assertEquals(1, resultPage.getContent().size());
        assertEquals("Math", resultPage.getContent().get(0).getSubject());
        verify(questionRepository, times(1)).findBySubject(eq("Math"), any(PageRequest.class));
    }
}
