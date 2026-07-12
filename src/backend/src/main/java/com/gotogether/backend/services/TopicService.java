package com.gotogether.backend.services;

import org.springframework.stereotype.Service;

import com.gotogether.backend.model.Topic;
import com.gotogether.backend.repository.TopicRepository;

import java.util.List;
import java.util.UUID;

/**
 * Service for managing topics, providing functions to create,
 * retrieve, and delete them.
 */
@Service
public class TopicService {

    private final TopicRepository repo;

    public TopicService(TopicRepository repo) {
        this.repo = repo;
    }

    /**
     * Retrieves a topic by its unique identifier.
     *
     * @param id the UUID of the topic
     * @return the requested Topic object
     * @throws RuntimeException if the topic cannot be found
     */
    public Topic getTopicById(UUID id) {
        if (id == null) {
            throw new RuntimeException("Topic ID must not be null.");
        }

        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Topic not found: " + id));
    }

    /**
     * Retrieves all topics available in the system.
     *
     * @return a list of all Topic objects
     */
    public List<Topic> getAllTopics() {
        return repo.findAll();
    }

    /**
     * Creates a new topic with the specified details.
     * Ensures that the topic name is unique and the background color (if provided)
     * is a valid hex code.
     *
     * @param name            the name of the topic
     * @param icon            an optional icon reference for the topic
     * @param backgroundColor an optional background color as a hex code (e.g.,
     *                        #FFFFFF or #FFFFFFFF)
     * @return the UUID of the created topic
     * @throws RuntimeException if the name is empty, already exists, or the color
     *                          format is invalid
     */
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

    /**
     * Deletes a topic by its unique identifier.
     *
     * @param id the UUID of the topic to delete
     * @return the UUID of the deleted topic
     * @throws RuntimeException if the topic does not exist
     */
    public UUID deleteTopic(UUID id) {
        if (id == null) {
            throw new RuntimeException("Topic ID must not be null.");
        }

        if (!repo.existsById(id)) {
            throw new RuntimeException("Topic not found: " + id);
        }

        repo.deleteById(id);
        return id;
    }

}