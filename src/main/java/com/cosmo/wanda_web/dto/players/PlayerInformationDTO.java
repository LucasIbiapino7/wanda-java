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
    private String code;
    private String code2;
    private List<BadgeDTO> badges;

    public PlayerInformationDTO() {
    }

    public PlayerInformationDTO(Player player) {
        id = player.getId();
        name = player.getUser().getName();
        numberOfMatches = player.getNumberOfMatches();
        numberOfWinners = player.getNumberOfWinners();
        if (!player.getUser().getFunctions().isEmpty()){
            for (Function function : player.getUser().getFunctions()) {
                if (function.getName().equals("jokenpo1")){
                    code = function.getFunction();
                } else if (function.getName().equals("jokenpo2")) {
                    code2 = function.getFunction();
                }
            }
        }
        badges = player.getUser().getBadges().stream().map(BadgeDTO::new).toList();
        characterUrl = player.getCharacterUrl();
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

    public String getCode2() {
        return code2;
    }

    public List<BadgeDTO> getBadges() {
        return badges;
    }

    public String getCharacterUrl() {
        return characterUrl;
    }
}
