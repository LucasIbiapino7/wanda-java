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
    private String nickname;

    @OneToOne
    @MapsId
    private User user;

    public Player() {
    }

    public Player(User user){
        numberOfMatches = 0;
        numberOfWinners = 0;
        nickname = "";
        this.user = user;
    }

    public Player(Long id, Integer numberOfMatches, Integer numberOfWinners, String nickname, User user) {
        this.id = id;
        this.numberOfMatches = numberOfMatches;
        this.numberOfWinners = numberOfWinners;
        this.nickname = nickname;
        this.user = user;
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

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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
