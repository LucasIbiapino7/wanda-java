package com.cosmo.wanda_web.services;

import com.cosmo.wanda_web.dto.tournament.SubscribeTournamentDTO;
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

    @Transactional
    public TournamentMinDTO subscribeTournament(SubscribeTournamentDTO dto) {
        Tournament tournament = tournamentRepository.findById(dto.tournamentId()).orElseThrow(
                () -> new TournamentException("Torneio nao encontrado!"));
        if (tournament.getCurrentParticipants() >= tournament.getMaxParticipants()){
            throw new TournamentException("Torneio com o número máximo de participantes!");
        }
        if (!tournament.getStatus().toString().equals("OPEN")){
            throw new TournamentException("Torneio não está aberto");
        }
        User participant = userService.authenticated();
        if (tournament.getAsPrivate() && !dto.password().equals(tournament.getPassword())){
            throw new TournamentException("A senha do torneio está incorreta!");
        }
        tournament.getUsers().add(participant);
        participant.getTournaments().add(tournament);
        tournament.setCurrentParticipants(tournament.getCurrentParticipants() + 1);
        tournament = tournamentRepository.save(tournament);
        return new TournamentMinDTO(tournament);
    }

    @Transactional(readOnly = true)
    public Page<TournamentMinDTO> findAllParticipating(Pageable pageable) {
        User user = userService.authenticated();
        Page<Tournament> result = tournamentRepository.findAllByUser(user.getId(), pageable);
        return result.map(TournamentMinDTO::new);
    }
}
