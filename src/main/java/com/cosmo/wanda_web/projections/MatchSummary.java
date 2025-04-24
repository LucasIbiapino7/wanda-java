package com.cosmo.wanda_web.projections;

public interface MatchSummary {
    Long       getId();
    PlayerInfo getPlayer1();
    PlayerInfo getPlayer2();
    PlayerInfo getWinner();
}
