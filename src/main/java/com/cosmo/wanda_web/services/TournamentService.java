package com.cosmo.wanda_web.services;

import com.cosmo.wanda_web.dto.match.PlayedMatchDTO;
import com.cosmo.wanda_web.dto.tournament.*;
import com.cosmo.wanda_web.entities.Tournament;
import com.cosmo.wanda_web.entities.TournamentStatus;
import com.cosmo.wanda_web.entities.User;
import com.cosmo.wanda_web.repositories.TournamentRepository;
import com.cosmo.wanda_web.services.exceptions.ResourceNotFoundException;
import com.cosmo.wanda_web.services.exceptions.TournamentException;
import com.cosmo.wanda_web.services.utils.JsonConverter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
public class TournamentService {

    @Autowired
    private UserService userService;

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private MatchService matchService;

    @Autowired
    private JsonConverter jsonConverter;

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

    @Transactional
    public void run(Long id) {
        Tournament tournament = tournamentRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Torneio nao encontrado!"));
        User user = userService.authenticated();
        if (!Objects.equals(user.getId(), tournament.getCreatorId())){
            throw new ResourceNotFoundException("Voce nao eh o criador do torneio");
        }

        List<Long> participants = new ArrayList<>(tournament.getUsers().stream().map(User::getId).toList());
        Collections.shuffle(participants);

        BracketTournament bracket = new BracketTournament();
        int roundNum = participants.size();
        int fase = 1;
        List<Long> currentParticipants = participants;

        while (currentParticipants.size() > 1){
            RoundTournamentDTO round = new RoundTournamentDTO();
            round.setName(describeRound(currentParticipants.size()));
            List<Long> nextRound = new ArrayList<>();

            for (int i = 0; i < currentParticipants.size(); i += 2) {
                Long player1 = currentParticipants.get(i);
                Long player2 = currentParticipants.get(i + 1);
                Long matchId = matchService.RunMatch(new PlayedMatchDTO(player1, player2));
                Long winner = matchService.winnerOfMatch(matchId);

                MatchResultTournamentDTO matchResult = new MatchResultTournamentDTO();
                matchResult.setPlayer1Id(player1);
                matchResult.setPlayer2Id(player2);
                matchResult.setPlayer1Name("player 1");
                matchResult.setPlayer2Name("player 2");
                matchResult.setMatchId(matchId);
                matchResult.setWinnerId(winner);
                matchResult.setWinnerNameId("vencedor");

                round.getMatches().add(matchResult);
                nextRound.add(winner);
            }

            bracket.getRounds().add(round);
            currentParticipants = nextRound;
            fase++;
        }

        String jsonData = jsonConverter.converterBracket(bracket);
        tournament.setBracketJson(jsonData);
        tournament.setWinnerId(currentParticipants.get(0));
        tournament.setStatus(TournamentStatus.FINISHED);
        tournamentRepository.save(tournament);
    }

    private String describeRound(int players) {
        return switch (players) {
            case 32 -> "Oitavas de Final";   // 32 jogadores -> 16 jogos
            case 16 -> "Oitavas de Final";   // 16->8 jogos
            case 8  -> "Quartas de Final";   // 8->4 jogos
            case 4  -> "Semifinal";          // 4->2 jogos
            case 2  -> "Final";              // 2->1 jogo
            default -> players + " players";
        };
    }
}
