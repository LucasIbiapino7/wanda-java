package com.cosmo.wanda_web.services;

import com.cosmo.wanda_web.entities.Tournament;
import com.cosmo.wanda_web.entities.TournamentStatus;
import com.cosmo.wanda_web.repositories.TournamentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TournamentSchedulerService {

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private TournamentService tournamentService;

    @Scheduled(fixedRate = 60_000)
    public void checkAndStartTournaments(){
        LocalDateTime now = LocalDateTime.now();

        List<Tournament> tournamentList = tournamentRepository
                .findByStatusAndStartTimeLessThanEqual(TournamentStatus.OPEN, now);

        for (Tournament tournament : tournamentList) {
            if (tournament.getCurrentParticipants() >= tournament.getMaxParticipants()){
                tournamentService.startTournament(tournament.getId());
            }else {
                tournament.setStartTime(tournament.getStartTime().plusMinutes(15));
                tournamentRepository.save(tournament);
            }
        }
    }

}
