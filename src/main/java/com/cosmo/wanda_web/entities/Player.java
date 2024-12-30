package com.cosmo.wanda_web.entities;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "tb_player")
public class Player {

    @Id
    private Long id;
    private Integer numberOfMatches;
    private Integer numberOfWinners;

    @OneToOne
    @MapsId
    private User user;

    public Player() {
    }

    public Player(Long id, Integer numberOfMatches, Integer numberOfWinners) {
        this.id = id;
        this.numberOfMatches = numberOfMatches;
        this.numberOfWinners = numberOfWinners;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Player player = (Player) o;

        return Objects.equals(id, player.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
