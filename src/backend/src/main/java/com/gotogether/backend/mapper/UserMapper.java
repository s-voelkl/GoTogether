package com.gotogether.backend.mapper;

import com.gotogether.backend.dto.UserDTO;
import com.gotogether.backend.model.Topic;
import com.gotogether.backend.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UserMapper {

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

    private long xpForLevel(int level, double a, double b) {
        return (long) (a * (1 - Math.pow(b, level)) / (1 - b));
    }

    private int calculateLevelXp(long xp, int level) {
        if (level == 1)
            return (int) xp;
        long xpForCurrentLevel = xpForLevel(level, 100.0, 1.15);
        return (int) (xp - xpForCurrentLevel);
    }
}
