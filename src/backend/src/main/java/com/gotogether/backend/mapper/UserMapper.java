package com.gotogether.backend.mapper;

import com.gotogether.backend.dto.UserDTO;
import com.gotogether.backend.model.Topic;
import com.gotogether.backend.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Mapper class for converting {@link User} entities to Data Transfer Objects
 * (DTOs).
 * <p>
 * This class handles mapping the internal {@link User} representation into a
 * {@link UserDTO}, including the calculation of the user's level and level
 * experience
 * points based on their total experience points.
 */
@Component
@RequiredArgsConstructor
public class UserMapper {

    /**
     * Converts a {@link User} entity to a {@link UserDTO}.
     *
     * @param user the {@link User} entity to map
     * @return a {@link UserDTO} representing the user, including derived level
     *         metrics
     */
    public UserDTO toDTO(User user) {
        List<Topic> interests = user.getInterests();
        return UserDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .socialBattery(user.getSocialBattery())
                .currency(user.getCurrency())
                .level(calculateLevel(user.getExperiencePoints()))
                .levelXp(calculateLevelXp(user.getExperiencePoints(), calculateLevel(user.getExperiencePoints())))
                .interests(interests == null ? null : interests.stream().map(Topic::getId).toList())
                .lastLogin(user.getLastLogin())
                .settings(user.getSettings())
                .build();
    }

    /**
     * Calculates the user's current level based on their total experience points.
     *
     * @param xp the total experience points of the user
     * @return the calculated level, capped at the maximum level
     */
    private int calculateLevel(long xp) {
        final double a = 100.0;
        final double b = 1.15;
        final int maxLevel = 100;

        int level = 1;
        while (level < maxLevel) {
            long requiredXp = xpForLevel(level + 1, a, b);
            if (xp < requiredXp)
                break;
            level++;
        }
        return level;
    }

    /**
     * Calculates the required total experience points to reach a specific level.
     *
     * @param level the target level
     * @param a     the scaling factor parameter (base xp)
     * @param b     the exponential base parameter (growth factor)
     * @return the required total experience points for the specified level
     */
    private long xpForLevel(int level, double a, double b) {
        return (long) (a * (1 - Math.pow(b, level)) / (1 - b));
    }

    /**
     * Calculates the user's progress within their current level.
     *
     * @param xp    the total experience points of the user
     * @param level the current level of the user
     * @return the experience points accumulated towards the next level
     */
    private int calculateLevelXp(long xp, int level) {
        if (level == 1)
            return (int) xp;
        long xpForCurrentLevel = xpForLevel(level, 100.0, 1.15);
        return (int) (xp - xpForCurrentLevel);
    }
}
