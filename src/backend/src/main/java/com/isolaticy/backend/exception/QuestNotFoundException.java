package com.isolaticy.backend.exception;

import java.util.UUID;

public class QuestNotFoundException extends RuntimeException {

    public QuestNotFoundException(UUID id) {
        super("Quest not found: " + id);
    }
}