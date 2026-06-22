package com.gotogether.backend.services;

import org.springframework.stereotype.Service;

import com.gotogether.backend.model.Topic;
import com.gotogether.backend.repository.TopicRepository;

import java.util.List;
import java.util.UUID;

@Service
public class TopicService {

    private final TopicRepository repo;

    public TopicService(TopicRepository repo) {
        this.repo = repo;
    }

    public Topic getTopicById(UUID id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Topic not found: " + id));
    }

    public List<Topic> getAllTopics() {
        return repo.findAll();
    }

    public UUID createTopic(String name, String icon, String backgroundColor) {
        if (name == null || name.trim().isEmpty()) {
            throw new RuntimeException("Topic name cannot be empty.");
        }

        String normalized = name.trim();

        // unique name
        if (repo.existsByName(normalized)) {
            throw new RuntimeException("Topic name already exists: " + normalized);
        }

        if (backgroundColor != null && !backgroundColor.isBlank()
                && !backgroundColor.matches("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{8})$")) {
            throw new RuntimeException("Invalid background color (expected #RRGGBB or #RRGGBBAA): " + backgroundColor);
        }

        Topic topic = new Topic(
                normalized,
                icon == null || icon.isBlank() ? null : icon.trim(),
                backgroundColor == null || backgroundColor.isBlank() ? null : backgroundColor.trim());
        return repo.save(topic).getId();
    }

    public UUID deleteTopic(UUID id) {
        if (!repo.existsById(id)) {
            throw new RuntimeException("Topic not found: " + id);
        }

        repo.deleteById(id);
        return id;
    }

}