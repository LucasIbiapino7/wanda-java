package com.cosmo.wanda_web.dto.players;

import com.cosmo.wanda_web.dto.function.FunctionResponseDto;
import com.cosmo.wanda_web.entities.Badge;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ProfileDTO {
    private Long id;
    private String name;
    private String nickname;
    private Integer numberOfMatches;
    private Integer numberOfWinners;
    private Integer winsTournaments;
    private String characterUrl;
    private List<BadgeDTO> badges = new ArrayList<>();
    private List<FunctionResponseDto> functions = new ArrayList<>();

    public ProfileDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Integer getNumberOfMatches() {
        return numberOfMatches;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNumberOfMatches(Integer numberOfMatches) {
        this.numberOfMatches = numberOfMatches;
    }

    public Integer getNumberOfWinners() {
        return numberOfWinners;
    }

    public void setNumberOfWinners(Integer numberOfWinners) {
        this.numberOfWinners = numberOfWinners;
    }

    public Integer getWinsTournaments() {
        return winsTournaments;
    }

    public void setWinsTournaments(Integer winsTournaments) {
        this.winsTournaments = winsTournaments;
    }

    public String getCharacterUrl() {
        return characterUrl;
    }

    public void setCharacterUrl(String characterUrl) {
        this.characterUrl = characterUrl;
    }

    public List<BadgeDTO> getBadges() {
        return badges;
    }

    public List<FunctionResponseDto> getFunctions() {
        return functions;
    }

    public void setFunctions(List<FunctionResponseDto> functions) {
        this.functions = functions;
    }

    public void addBadges(Set<Badge> badges){
        for (Badge badge : badges) {
            this.badges.add(new BadgeDTO(badge));
        }
    }
}
