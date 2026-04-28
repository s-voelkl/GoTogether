package com.isolaticy.backend.service;

import com.isolaticy.backend.dto.QuestCreateRequest;
import com.isolaticy.backend.dto.QuestResponse;
import com.isolaticy.backend.exception.QuestNotFoundException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.springframework.stereotype.Service;

@Service
public class QuestService {

    private final ConcurrentMap<UUID, QuestResponse> quests = new ConcurrentHashMap<>();

    public QuestService() {
        QuestResponse seedQuest = new QuestResponse(
                UUID.fromString("11111111-1111-1111-1111-111111111111"),
                "Coffee Walk",
                "Take a short walk to a local cafe and start a conversation.",
                "social",
                Instant.parse("2026-01-01T10:00:00Z"));
        quests.put(seedQuest.id(), seedQuest);
    }

    public List<QuestResponse> findAll() {
        return new ArrayList<>(quests.values());
    }

    public QuestResponse findById(UUID id) {
        QuestResponse quest = quests.get(id);
        if (quest == null) {
            throw new QuestNotFoundException(id);
        }
        return quest;
    }

    public QuestResponse create(QuestCreateRequest request) {
        QuestResponse quest = new QuestResponse(
                UUID.randomUUID(),
                request.title(),
                request.description(),
                request.category(),
                Instant.now());
        quests.put(quest.id(), quest);
        return quest;
    }
}