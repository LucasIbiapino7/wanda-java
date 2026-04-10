package com.cosmo.wanda_web.dto.tournament;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class TournamentUpdateDTO {
    @NotBlank
    @Size(min = 3, max = 40)
    private String name;
    @NotBlank
    @Size(min = 5, max = 80)
    private String description;
    private LocalDateTime startTime;

    public TournamentUpdateDTO() {
    }

    public TournamentUpdateDTO(String name, String description, LocalDateTime startTime) {
        this.name = name;
        this.description = description;
        this.startTime = startTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }
}
