package com.cosmo.wanda_web.entities;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "tb_match")
public class Match {

    @EmbeddedId
    private MatchPK id = new MatchPK();

    @Column(columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private Instant moment;
    private Long winner;

    public Match() {
    }

    public Match(User player1, User player2,  Instant moment, Long winner) {
        id.setPlayer1(player1);
        id.setPlayer2(player2);
        this.moment = moment;
        this.winner = winner;
    }

    public User getPlayer1() {
        return id.getPlayer1();
    }

    public void setPlayer1(User player1) {
        id.setPlayer1(player1);
    }

    public User getPlayer2() {
        return id.getPlayer2();
    }

    public void setPlayer2(User player2) {
        id.setPlayer2(player2);
    }

    public Instant getMoment() {
        return moment;
    }

    public void setMoment(Instant moment) {
        this.moment = moment;
    }

    public Long getWinner() {
        return winner;
    }

    public void setWinner(Long winner) {
        this.winner = winner;
    }
}
