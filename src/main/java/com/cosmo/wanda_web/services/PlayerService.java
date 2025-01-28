package com.cosmo.wanda_web.services;

import com.cosmo.wanda_web.dto.players.ProfileDTO;
import com.cosmo.wanda_web.entities.Function;
import com.cosmo.wanda_web.entities.Match;
import com.cosmo.wanda_web.entities.Player;
import com.cosmo.wanda_web.entities.User;
import com.cosmo.wanda_web.repositories.FunctionRepository;
import com.cosmo.wanda_web.repositories.PlayerRepository;
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
           } else if (match.getWinner().equals(user2)) {
               player1.setNumberOfWinners(player2.getNumberOfWinners() + 1);
           }
       }
    }
}
