package com.cosmo.wanda_web.dto.players;

import com.cosmo.wanda_web.entities.Player;

import java.util.List;

public class PlayerInformationDTO {
    private Long id;
    private String name;
    private Integer numberOfMatches;
    private Integer numberOfWinners;
    private String code;
    private List<BadgeDTO> badges;

    public PlayerInformationDTO() {
    }

    public PlayerInformationDTO(Player player) {
        id = player.getId();
        name = player.getUser().getName();
        numberOfMatches = player.getNumberOfMatches();
        numberOfWinners = player.getNumberOfWinners();
        if (!player.getUser().getFunctions().isEmpty()){
            code = player.getUser().getFunctions().get(0).getFunction();
        }
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

    public String getCode() {
        return code;
    }

    public List<BadgeDTO> getBadges() {
        return badges;
    }
}
