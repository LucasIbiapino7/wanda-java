package com.cosmo.wanda_web.services;

import com.cosmo.wanda_web.dto.tournament.TournamentDTO;
import com.cosmo.wanda_web.entities.Tournament;
import com.cosmo.wanda_web.entities.TournamentStatus;
import com.cosmo.wanda_web.entities.User;
import com.cosmo.wanda_web.repositories.TournamentRepository;
import com.cosmo.wanda_web.services.exceptions.TournamentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class TournamentService {

    @Autowired
    private UserService userService;

    @Autowired
    private TournamentRepository tournamentRepository;

    @Transactional
    public TournamentDTO create(TournamentDTO dto) {
        System.out.println(dto.getAsPrivate());
        User user = userService.authenticated(); // Pega o usuário autenticado

        Long count = tournamentRepository.countOpenTournaments(user.getId());

        System.out.println("COUNT = " + count);

        if (count >= 1){
            throw new TournamentException("O usuário já tem um torneio criado!");
        }

        Tournament tournament = new Tournament();
        tournament.setName(dto.getName());
        tournament.setDescription(dto.getDescription());
        tournament.setStartTime(dto.getStartTime());
        tournament.setMaxParticipants(dto.getMaxParticipants());
        tournament.setCreatorId(user.getId());
        tournament.setCreatedAt(LocalDateTime.now());
        tournament.setCurrentParticipants(0);
        tournament.setStatus(TournamentStatus.OPEN);
        tournament.setAsPrivate(dto.getAsPrivate());
        tournament.setWinnerId(null);
        if (dto.getAsPrivate()){
            tournament.setPassword(dto.getPassword());
        }
        tournament = tournamentRepository.save(tournament);
        return new TournamentDTO(tournament);
    }
}
