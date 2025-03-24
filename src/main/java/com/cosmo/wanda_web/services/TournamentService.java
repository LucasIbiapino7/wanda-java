package com.cosmo.wanda_web.services;

import com.cosmo.wanda_web.dto.tournament.TournamentCreateDTO;
import com.cosmo.wanda_web.dto.tournament.TournamentMinDTO;
import com.cosmo.wanda_web.dto.tournament.TournamentWithParticipantsDTO;
import com.cosmo.wanda_web.entities.Tournament;
import com.cosmo.wanda_web.entities.TournamentStatus;
import com.cosmo.wanda_web.entities.User;
import com.cosmo.wanda_web.repositories.TournamentRepository;
import com.cosmo.wanda_web.services.exceptions.TournamentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public TournamentCreateDTO create(TournamentCreateDTO dto) {
        User user = userService.authenticated(); // Pega o usuário autenticado
        Long count = tournamentRepository.countOpenTournaments(user.getId());
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
        return new TournamentCreateDTO(tournament);
    }

    @Transactional(readOnly = true) //Preciso melhorar
    public TournamentWithParticipantsDTO findByIdWithParticipants(Long id) {
        Tournament tournament = tournamentRepository.findByIdWithParticipants(id);
        if (tournament == null){
            throw new TournamentException("O torneio não existe!");
        }
        return new TournamentWithParticipantsDTO(tournament);
    }

    @Transactional(readOnly = true)
    public Page<TournamentMinDTO> findAll(String searchTerm, Pageable pageable) {
        Page<Tournament> result = tournamentRepository.findByNameWithOrdering(searchTerm, pageable);
        return result.map(TournamentMinDTO::new);
    }
}
