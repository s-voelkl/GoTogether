package com.gotogether.backend.controller;

import com.gotogether.backend.dto.CreateTopicDTO;
import com.gotogether.backend.services.TopicService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * REST controller for managing topics.
 * <p>
 * Provides endpoints for creating, retrieving, and deleting topics.
 */
@RestController
@RequestMapping("/api/topics")
public class TopicController {

    private final TopicService service;

    /**
     * Constructs a new TopicController with the given TopicService.
     *
     * @param service the service used for topic operations
     */
    public TopicController(TopicService service) {
        this.service = service;
    }

    /**
     * Retrieves a topic by its unique identifier.
     *
     * @param id the UUID of the topic to retrieve
     * @return a ResponseEntity containing the TopicDTO if found, or a 404 NOT FOUND
     *         status with an error message
     * @throws RuntimeException if the topic is not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getTopicById(@PathVariable UUID id) {
        try {
            return ResponseEntity.ok(service.getTopicById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Retrieves a list of all topics.
     *
     * @return a ResponseEntity containing a list of TopicDTOs, or a 500 INTERNAL
     *         SERVER ERROR status on failure
     * @throws RuntimeException if an error occurs during retrieval
     */
    @GetMapping
    public ResponseEntity<?> getAllTopics() {
        try {
            return ResponseEntity.ok(service.getAllTopics());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Creates a new topic.
     *
     * @param request the data transfer object containing the topic details
     * @return a ResponseEntity containing the UUID of the newly created topic, or a
     *         400 BAD REQUEST status on validation failure
     * @throws RuntimeException if the topic cannot be created
     */
    @PostMapping
    public ResponseEntity<?> createTopic(@RequestBody CreateTopicDTO request) {
        try {
            UUID id = service.createTopic(request.getName(), request.getIcon(), request.getBackgroundColor());
            return ResponseEntity.status(HttpStatus.CREATED).body(id);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Deletes a topic by its unique identifier.
     *
     * @param id the UUID of the topic to delete
     * @return a ResponseEntity containing the UUID of the deleted topic, or a 404
     *         NOT FOUND status with an error message
     * @throws RuntimeException if the topic cannot be found or deleted
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTopic(@PathVariable UUID id) {
        try {
            UUID deletedId = service.deleteTopic(id);
            return ResponseEntity.ok(deletedId);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

}
