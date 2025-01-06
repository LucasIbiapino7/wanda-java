package com.cosmo.wanda_web.dto.match;

public class PlayedMatchDTO {

    private Long playerId1;
    private Long playerId2;

    public PlayedMatchDTO() {
    }

    public PlayedMatchDTO(Long playerId1, Long playerId2) {
        this.playerId1 = playerId1;
        this.playerId2 = playerId2;
    }

    public Long getPlayerId1() {
        return playerId1;
    }

    public void setPlayerId1(Long playerId1) {
        this.playerId1 = playerId1;
    }

    public Long getPlayerId2() {
        return playerId2;
    }

    public void setPlayerId2(Long playerId2) {
        this.playerId2 = playerId2;
    }
}
