package com.poludnikiewicz.bugtracker.registration.token;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ConfirmationTokenServiceTest {

    @InjectMocks
    private ConfirmationTokenService service;
    @Mock
    private ConfirmationTokenRepository repository;

    @Test
    @DisplayName("Should invoke save method of ConfirmationTokenRepository")
    void saveConfirmationToken() {
        ConfirmationToken token = mock(ConfirmationToken.class);
        service.saveConfirmationToken(token);
        verify(repository).save(token);
    }

    @Test
    @DisplayName("Should return findByToken(String token) of ConfirmationTokenRepository")
    void getToken() {
        String token = UUID.randomUUID().toString();
        service.getToken(token);
        verify(repository).findByToken(token);
    }

    @Test
    @DisplayName("Should return updateConfirmedAt(String token) of ConfirmationTokenRepository")
    void setConfirmedAt() {
        String token = UUID.randomUUID().toString();
        service.setConfirmedAt(token);
        verify(repository).updateConfirmedAt(eq(token), any(LocalDateTime.class));
    }
}