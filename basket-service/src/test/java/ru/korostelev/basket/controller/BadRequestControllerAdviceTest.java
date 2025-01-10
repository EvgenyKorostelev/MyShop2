package ru.korostelev.basket.controller;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Модульные тесты BadRequestControllerAdvice")
public class BadRequestControllerAdviceTest {

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private BadRequestControllerAdvice adviceController;

    @Test
    void handleBindExceptionReturnsBadRequestResponse() {
        Locale locale = Locale.ENGLISH;
        String expectedTitle = "Invalid request data";
        BindException exception = new BindException(this, "testObject");
        exception.addError(new ObjectError("field1", "Field 1 is required"));
        exception.addError(new ObjectError("field2", "Field 2 must be positive"));

        when(messageSource.getMessage("errors.400.title", new Object[0], "errors.400.title", locale))
                .thenReturn(expectedTitle);

        ResponseEntity<ProblemDetail> response = adviceController.handleBindException(exception, locale);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ProblemDetail problemDetail = response.getBody();
        assert problemDetail != null;
        assertEquals(HttpStatus.BAD_REQUEST.value(), problemDetail.getStatus());
        assertEquals(expectedTitle, problemDetail.getDetail());
        assertEquals(List.of("Field 1 is required", "Field 2 must be positive"),
                Objects.requireNonNull(problemDetail.getProperties()).get("errors"));

        verify(messageSource).getMessage(
                "errors.400.title", new Object[0], "errors.400.title", locale);
    }
}
