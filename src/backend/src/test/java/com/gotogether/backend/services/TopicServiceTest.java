package com.gotogether.backend.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.gotogether.backend.model.Topic;
import com.gotogether.backend.repository.TopicRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TopicServiceTest {

    @Mock
    private TopicRepository topicRepository;

    @InjectMocks
    private TopicService topicService;

    @Test
    void getTopicById_TopicExists_ReturnsTopic() {
        // Arrange
        Topic mockTopic = new Topic("Programming");
        when(topicRepository.findById(mockTopic.getId())).thenReturn(Optional.of(mockTopic));

        // Act
        Topic result = topicService.getTopicById(mockTopic.getId());

        // Assert
        assertNotNull(result);
        assertEquals(mockTopic.getId(), result.getId());
        assertEquals("Programming", result.getName());
        verify(topicRepository, times(1)).findById(mockTopic.getId());
    }

    @Test
    void getTopicById_TopicDoesNotExist_ThrowsRuntimeException() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        when(topicRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            topicService.getTopicById(nonExistentId);
        });
        verify(topicRepository, times(1)).findById(nonExistentId);
    }

    @Test
    void getAllTopics_ReturnsAllTopics() {
        // Arrange
        List<Topic> topics = Arrays.asList(new Topic("T1"), new Topic("T2"));
        when(topicRepository.findAll()).thenReturn(topics);

        // Act
        List<Topic> result = topicService.getAllTopics();

        // Assert
        assertEquals(2, result.size());
        verify(topicRepository, times(1)).findAll();
    }

    @Test
    void createTopic_ValidName_ReturnsId() {
        // Arrange
        String name = "NewTopic";
        when(topicRepository.existsByName(name.trim())).thenReturn(false);
        // We ensure that the saved topic keeps the generated ID or generates one
        when(topicRepository.save(any(Topic.class))).thenAnswer(invocation -> {
            Topic topic = invocation.getArgument(0);
            topic.setId(UUID.randomUUID());
            return topic;
        });

        // Act
        UUID resultId = topicService.createTopic(name);

        // Assert
        assertNotNull(resultId);
        verify(topicRepository, times(1)).existsByName(name.trim());
        verify(topicRepository, times(1)).save(any(Topic.class));
    }

    @Test
    void createTopic_EmptyName_ThrowsException() {
        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> topicService.createTopic(" "));
        verify(topicRepository, never()).save(any());
    }

    @Test
    void createTopic_NullName_ThrowsException() {
        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> topicService.createTopic(null));
        verify(topicRepository, never()).save(any());
    }

    @Test
    void createTopic_DuplicateName_ThrowsException() {
        // Arrange
        String name = "ExistingTopic";
        when(topicRepository.existsByName(name.trim().toLowerCase())).thenReturn(true);

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> topicService.createTopic(name));
        verify(topicRepository, never()).save(any());
    }

    @Test
    void deleteTopic_ExistingId_ReturnsId() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(topicRepository.existsById(id)).thenReturn(true);

        // Act
        UUID resultId = topicService.deleteTopic(id);

        // Assert
        assertEquals(id, resultId);
        verify(topicRepository, times(1)).deleteById(id);
    }

    @Test
    void deleteTopic_NonExistentId_ThrowsException() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(topicRepository.existsById(id)).thenReturn(false);

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> topicService.deleteTopic(id));
        verify(topicRepository, never()).deleteById(any());
    }

}