package com.gotogether.backend.mapper;

import com.gotogether.backend.dto.UserDTO;
import com.gotogether.backend.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper {

    public UserDTO toDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .password(user.getPassword())
                .email(user.getEmail())
                .socialBattery(user.getSocialBattery())
                .currency(user.getCurrency())
                .experiencePoints(user.getExperiencePoints())
                .level(calculateLevel(user.getExperiencePoints()))
                .interests(user.getInterests())
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
            if (xp < requiredXp) break;
            level++;
        }
        return level;
    }

    private long xpForLevel(int level, double a, double b) {
        return (long) (a * (1 - Math.pow(b, level)) / (1 - b));
    }
}
