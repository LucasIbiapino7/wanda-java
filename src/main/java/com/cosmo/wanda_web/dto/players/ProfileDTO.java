package com.cosmo.wanda_web.dto.players;

public class ProfileDTO {
    private Long id;
    private String nickname;
    private Integer numberOfMatches;
    private Integer numberOfWinners;
    private String function;

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
}
