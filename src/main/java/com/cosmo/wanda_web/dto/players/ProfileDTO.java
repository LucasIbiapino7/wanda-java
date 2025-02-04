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
    private String function;
    private List<BadgeDTO> badges = new ArrayList<>();

    public ProfileDTO() {
    }

    public ProfileDTO(Long id, String nickname, Integer numberOfMatches, Integer numberOfWinners, String function) {
        this.id = id;
        this.nickname = nickname;
        this.numberOfMatches = numberOfMatches;
        this.numberOfWinners = numberOfWinners;
        this.function = function;
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

    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
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
