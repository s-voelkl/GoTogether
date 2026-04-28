package com.isolaticy.backend.controller;

import com.isolaticy.backend.dto.QuestCreateRequest;
import com.isolaticy.backend.dto.QuestResponse;
import com.isolaticy.backend.service.QuestService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/quests")
public class QuestController {

    private final QuestService questService;

    public QuestController(QuestService questService) {
        this.questService = questService;
    }

    @GetMapping
    public List<QuestResponse> listQuests() {
        return questService.findAll();
    }

    @GetMapping("/{id}")
    public QuestResponse getQuest(@PathVariable UUID id) {
        return questService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public QuestResponse createQuest(@Valid @RequestBody QuestCreateRequest request) {
        return questService.create(request);
    }
}