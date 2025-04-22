package com.cosmo.wanda_web.services.utils;

import com.cosmo.wanda_web.entities.Player;
import com.cosmo.wanda_web.entities.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

@Component
public class PlayerWithCharacter {

    private static final Random RANDOM = new Random();

    private static final List<String> CHARACTERS_URLS = List.of("p1.png", "p2.png", "p3.png", "p4.png", "p5.png", "p6.png");

    public Player createPlayer(User newUser) {
        Player player = new Player(newUser);
        String character = randomCharacter();
        player.setCharacterUrl(character);
        player.setWinsTournaments(0);
        return player;
    }

    private String randomCharacter() {
        return CHARACTERS_URLS.get(RANDOM.nextInt(0, CHARACTERS_URLS.size()));
    }
}
