package com.cosmo.wanda_web.dto.classroom;

public class DashboardRankingDTO {

    private Integer position;
    private Long userId;
    private String userName;
    private Long wins;

    public DashboardRankingDTO(Integer position, Long userId, String userName, Long wins) {
        this.position = position;
        this.userId = userId;
        this.userName = userName;
        this.wins = wins;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Long getWins() {
        return wins;
    }

    public void setWins(Long wins) {
        this.wins = wins;
    }
}