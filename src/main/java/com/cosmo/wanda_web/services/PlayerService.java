package com.cosmo.wanda_web.services;

import com.cosmo.wanda_web.dto.players.ProfileDTO;
import com.cosmo.wanda_web.entities.*;
import com.cosmo.wanda_web.repositories.BadgeRepository;
import com.cosmo.wanda_web.repositories.FunctionRepository;
import com.cosmo.wanda_web.repositories.PlayerRepository;
import com.cosmo.wanda_web.repositories.UserRepository;
import com.cosmo.wanda_web.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class PlayerService {

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private FunctionRepository functionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BadgeRepository badgeRepository;

    @Autowired
    private UserService userService;

    @Transactional
    public ProfileDTO findProfileByUser() {
        User user = userService.authenticated();

        Player player = playerRepository.findById(user.getId()).orElseThrow(
                () -> new ResourceNotFoundException("Resource not found"));

        String code = "";
        Optional<Function> function = functionRepository.findByPlayerId(user.getId());
        if (function.isPresent()){
            code = function.get().getFunction();
        }

        ProfileDTO profileDTO = new ProfileDTO();
        profileDTO.setId(player.getId());
        profileDTO.setNickname(player.getNickname());
        profileDTO.setNumberOfMatches(player.getNumberOfMatches());
        profileDTO.setNumberOfWinners(player.getNumberOfWinners());
        profileDTO.setFunction(code);

        return profileDTO;
    }

    @Transactional
    public void updateWinners(User user1, User user2, Match match) {

        Player player1 = playerRepository.findById(user1.getId()).orElseThrow(
                () -> new ResourceNotFoundException("Player not found"));

        Player player2 = playerRepository.findById(user2.getId()).orElseThrow(
                () -> new ResourceNotFoundException("Player not found"));

        player1.setNumberOfMatches(player1.getNumberOfMatches() + 1);
        player2.setNumberOfMatches(player2.getNumberOfMatches() + 1);

       if (match.getWinner() != null){
           if (match.getWinner().equals(user1)){
               player1.setNumberOfWinners(player1.getNumberOfWinners() + 1);
               verifyBadges(user1, player1);
           } else if (match.getWinner().equals(user2)) {
               player1.setNumberOfWinners(player2.getNumberOfWinners() + 1);
               verifyBadges(user2, player2);
           }
       }
    }

    @Transactional
    private void verifyBadges(User user, Player player) {
        if (player.getNumberOfWinners() >= 10){
            Badge badge = badgeRepository.findByName("Ninja Condicional!").orElseThrow(
                    () -> new ResourceNotFoundException("Badge Not Found"));
            user.getBadges().add(badge);
            userRepository.save(user);
        } else if (player.getNumberOfWinners() >= 5) {
            Badge badge = badgeRepository.findByName("Mestre do If/Else!").orElseThrow(
                    () -> new ResourceNotFoundException("Badge Not Found"));
            user.getBadges().add(badge);
            userRepository.save(user);
        } else if (player.getNumberOfWinners() >= 3) {
            Badge badge = badgeRepository.findByName("Aprendiz condicional!").orElseThrow(
                    () -> new ResourceNotFoundException("Badge Not Found"));
            user.getBadges().add(badge);
            userRepository.save(user);
        }
    }
}
