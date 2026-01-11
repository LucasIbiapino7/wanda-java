package com.cosmo.wanda_web.dto.players;

import com.cosmo.wanda_web.entities.Function;
import com.cosmo.wanda_web.entities.Player;

import java.util.List;

public class PlayerInformationDTO {
    private Long id;
    private String name;
    private String characterUrl;
    private Integer numberOfMatches;
    private Integer numberOfWinners;
    private String nickName;
    private List<BadgeDTO> badges;

    public PlayerInformationDTO() {
    }

    public PlayerInformationDTO(Player player) {
        id = player.getId();
        name = player.getUser().getName();
        numberOfMatches = player.getNumberOfMatches();
        numberOfWinners = player.getNumberOfWinners();
        badges = player.getUser().getBadges().stream().map(BadgeDTO::new).toList();
        characterUrl = player.getCharacterUrl();
        nickName = player.getNickname();
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

    public List<BadgeDTO> getBadges() {
        return badges;
    }

    public String getCharacterUrl() {
        return characterUrl;
    }

    public String getNickName() {
        return nickName;
    }
}
