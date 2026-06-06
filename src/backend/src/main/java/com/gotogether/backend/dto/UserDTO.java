package com.gotogether.backend.dto;

import com.gotogether.backend.model.Settings;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class UserDTO {

    private UUID id;

    private String name;

    private String email;

    private int socialBattery;

    private int currency;

    private int level; // derived

    private int levelXp; // derived

    private List<UUID> interests;

    private LocalDateTime lastLogin;

    @Embedded
    private Settings settings;
}
