package com.cosmo.wanda_web.dto.players;

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
    private String function1;
    private String function2;
    private List<BadgeDTO> badges = new ArrayList<>();

    public ProfileDTO() {
    }

    public ProfileDTO(Long id, String name, String nickname, Integer numberOfMatches, Integer numberOfWinners, Integer winsTournaments, String characterUrl, String function1, String function2) {
        this.id = id;
        this.name = name;
        this.nickname = nickname;
        this.numberOfMatches = numberOfMatches;
        this.numberOfWinners = numberOfWinners;
        this.winsTournaments = winsTournaments;
        this.characterUrl = characterUrl;
        this.function1 = function1;
        this.function2 = function2;
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

    public String getFunction1() {
        return function1;
    }

    public void setFunction1(String function1) {
        this.function1 = function1;
    }

    public String getFunction2() {
        return function2;
    }

    public void setFunction2(String function2) {
        this.function2 = function2;
    }

    public List<BadgeDTO> getBadges() {
        return badges;
    }

    public void addBadges(Set<Badge> badges){
        for (Badge badge : badges) {
            this.badges.add(new BadgeDTO(badge));
        }
    }
}
