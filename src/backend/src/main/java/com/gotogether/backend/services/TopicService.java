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

    public UUID createTopic(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new RuntimeException("Topic name cannot be empty: " + name);
        }

        // unique name
        if (repo.existsByName(name)) {
            throw new RuntimeException("Topic name already exists: " + name);
        }

        Topic topic = new Topic(name);
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