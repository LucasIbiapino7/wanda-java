package com.cosmo.wanda_web.dto.players;

import com.cosmo.wanda_web.entities.Player;

import java.util.List;

public class PlayerMinDTO {
    private Long id;
    private String name;
    private Integer numberOfMatches;
    private Integer numberOfWinners;
    private Integer winsTournaments;
    private List<BadgeDTO> badges;

    public PlayerMinDTO() {
    }

    public PlayerMinDTO(Player player) {
        id = player.getId();
        name = player.getUser().getName();
        numberOfMatches = player.getNumberOfMatches();
        numberOfWinners = player.getNumberOfWinners();
        winsTournaments = player.getWinsTournaments();
        badges = player.getUser().getBadges().stream().map(BadgeDTO::new).toList();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getNumberOfMatches() {
        return numberOfMatches;
    }

    public Integer getNumberOfWinners() {
        return numberOfWinners;
    }

    public Integer getWinsTournaments() {
        return winsTournaments;
    }

    public List<BadgeDTO> getBadges() {
        return badges;
    }
}
