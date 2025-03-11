package com.cosmo.wanda_web.utils;

import com.cosmo.wanda_web.entities.Player;
import com.cosmo.wanda_web.entities.User;
import com.cosmo.wanda_web.repositories.PlayerRepository;
import com.cosmo.wanda_web.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class TestDataLoader implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {

        User user = userRepository.findById(1L).get();
        playerRepository.save(new Player(user));

        User user2 = userRepository.findById(2L).get();
        playerRepository.save(new Player(user2));

        User user3 = userRepository.findById(3L).get();
        playerRepository.save(new Player(user3));

    }
}
