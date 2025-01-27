package com.cosmo.wanda_web.services;

import com.cosmo.wanda_web.dto.players.ProfileDTO;
import com.cosmo.wanda_web.entities.Function;
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
}
