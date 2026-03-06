package com.cosmo.wanda_web.services;

import com.cosmo.wanda_web.entities.Tournament;
import com.cosmo.wanda_web.entities.TournamentStatus;
import com.cosmo.wanda_web.repositories.TournamentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TournamentSchedulerService {

    private static final Logger log = LoggerFactory.getLogger(TournamentSchedulerService.class);

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private TournamentService tournamentService;

    @Scheduled(fixedRate = 60_000)
    public void checkAndStartTournaments(){
        LocalDateTime now = LocalDateTime.now();

        List<Tournament> tournamentList = tournamentRepository
                .findByStatusAndStartTimeLessThanEqual(TournamentStatus.OPEN, now);

        log.info("Scheduler verificou torneios. total encontrado={}", tournamentList.size());

        for (Tournament tournament : tournamentList) {
            if (tournament.getCurrentParticipants() >= tournament.getMaxParticipants()){
                log.info("Torneio iniciado pelo scheduler. torneoId={}, participantes={}",
                        tournament.getId(), tournament.getCurrentParticipants());
                tournamentService.startTournament(tournament.getId());
            }else {
                log.info("Torneio adiado por falta de participantes. torneoId={}, participantesAtuais={}, maxParticipantes={}, novoHorario={}",
                        tournament.getId(),
                        tournament.getCurrentParticipants(),
                        tournament.getMaxParticipants(),
                        tournament.getStartTime().plusMinutes(15));
                tournament.setStartTime(tournament.getStartTime().plusMinutes(15));
                tournamentRepository.save(tournament);
            }
        }
    }

}
