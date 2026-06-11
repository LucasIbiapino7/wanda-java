package com.cosmo.wanda_web.services;

import com.cosmo.wanda_web.entities.Game;
import com.cosmo.wanda_web.entities.Tournament;
import com.cosmo.wanda_web.entities.TournamentStatus;
import com.cosmo.wanda_web.entities.User;
import com.cosmo.wanda_web.infra.GameEngine;
import com.cosmo.wanda_web.infra.MatchOrchestrator;
import com.cosmo.wanda_web.infra.dtos.MatchResult;
import com.cosmo.wanda_web.repositories.MatchRepository;
import com.cosmo.wanda_web.repositories.TournamentRepository;
import com.cosmo.wanda_web.services.exceptions.MatchExecutionException;
import com.cosmo.wanda_web.services.utils.JsonConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Testes de resiliência do torneio (Nível 1 — sem Docker, sem Python real).
 *
 * Exercita o fluxo de {@code running} através do método público {@code startTournament},
 * com o {@code matchOrchestrator} mockado pra simular os cenários:
 *   - todas as partidas funcionam            → torneio normal
 *   - partida falha e o retry recupera        → torneio normal (sem W.O.)
 *   - partida falha em todas as tentativas    → W.O. por sorteio, torneio segue
 *   - erro catastrófico (não é de partida)    → torneio marcado como ERROR
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TournamentServiceResilienceTest {

    private static final Long TOURNAMENT_ID = 100L;

    @Mock private TournamentRepository tournamentRepository;
    @Mock private MatchOrchestrator matchOrchestrator;
    @Mock private MatchRepository matchRepository;
    @Mock private PlayerService playerService;
    @Mock private JsonConverter jsonConverter;
    @Mock private NotificationService notificationService;

    // GameEngine usado no caminho de W.O. (matchOrchestrator.getEngine(...).walkoverReplayJson(...))
    @Mock private GameEngine gameEngine;

    @Spy
    @InjectMocks
    private TournamentService service;

    private Tournament tournament;

    @BeforeEach
    void setUp() {
        tournament = tournamentWith4Players();

        // startTournament: encontra o torneio e consegue "iniciar"
        when(tournamentRepository.findById(TOURNAMENT_ID)).thenReturn(Optional.of(tournament));
        when(tournamentRepository.tryStart(TOURNAMENT_ID)).thenReturn(1);

        // caminho de W.O.: getEngine retorna o engine mockado, que devolve um replay qualquer
        when(matchOrchestrator.getEngine(anyString())).thenReturn(gameEngine);
        when(gameEngine.walkoverReplayJson(any(), any(), any(), anyString())).thenReturn("{\"type\":\"WALKOVER\"}");
    }

    @Test
    void quandoTodasAsPartidasFuncionam_torneioFinalizaNormalmente() {
        // partida sempre funciona: vencedor é o primeiro jogador do confronto
        when(matchOrchestrator.run(any(), any(), any()))
                .thenAnswer(inv -> new MatchResult(inv.getArgument(0), "{}"));

        service.startTournament(TOURNAMENT_ID);

        // 4 jogadores -> 3 partidas (2 na 1a fase + final)
        verify(matchOrchestrator, times(3)).run(any(), any(), any());
        verify(playerService, times(3)).updateWinners(any(), any(), any());
        verify(service, never()).sortearVencedor(any(), any());           // nenhum W.O.
        verify(matchOrchestrator, never()).getEngine(anyString());        // engine de W.O. não usado
        assertEquals(TournamentStatus.FINISHED, tournament.getStatus());
    }

    @Test
    void quandoPartidaFalhaDuasVezesERecuperaNaTerceira_naoHaWalkover() {
        // 1a partida: falha, falha, sucesso; demais partidas: sucesso direto
        when(matchOrchestrator.run(any(), any(), any()))
                .thenThrow(new MatchExecutionException("falha transitória 1"))
                .thenThrow(new MatchExecutionException("falha transitória 2"))
                .thenAnswer(inv -> new MatchResult(inv.getArgument(0), "{}"));

        service.startTournament(TOURNAMENT_ID);

        // 2 falhas + 3 sucessos = 5 chamadas ao run
        verify(matchOrchestrator, times(5)).run(any(), any(), any());
        verify(service, never()).sortearVencedor(any(), any());           // o retry salvou: sem W.O.
        verify(playerService, times(3)).updateWinners(any(), any(), any());
        assertEquals(TournamentStatus.FINISHED, tournament.getStatus());
    }

    @Test
    void quandoPartidaFalhaEmTodasAsTentativas_decidePorSorteioEContinua() {
        // toda partida falha sempre -> esgota as 3 tentativas em cada confronto
        when(matchOrchestrator.run(any(), any(), any()))
                .thenThrow(new MatchExecutionException("função quebrada"));

        // sorteio determinístico: o "vencedor" é sempre o primeiro do confronto
        doAnswer(inv -> inv.getArgument(0)).when(service).sortearVencedor(any(), any());

        service.startTournament(TOURNAMENT_ID);

        // 3 partidas x 3 tentativas = 9 chamadas ao run
        verify(matchOrchestrator, times(9)).run(any(), any(), any());
        // uma decisão por sorteio em cada uma das 3 partidas
        verify(service, times(3)).sortearVencedor(any(), any());
        verify(gameEngine, times(3)).walkoverReplayJson(any(), any(), any(), anyString());
        // W.O. NÃO conta estatística
        verify(playerService, never()).updateWinners(any(), any(), any());
        // o torneio chega ao fim, não fica em ERROR
        assertEquals(TournamentStatus.FINISHED, tournament.getStatus());
    }

    @Test
    void quandoErroCatastrofico_torneioVaiParaError() {
        // erro que NÃO é de execução de partida (não deve dar retry nem W.O.)
        when(matchOrchestrator.run(any(), any(), any()))
                .thenThrow(new IllegalStateException("engine quebrada / infra"));

        service.startTournament(TOURNAMENT_ID);

        // sem retry pra erro catastrófico: 1 chamada e aborta
        verify(matchOrchestrator, times(1)).run(any(), any(), any());
        verify(service, never()).sortearVencedor(any(), any());
        verify(playerService, never()).updateWinners(any(), any(), any());
        // notificação de fim NÃO acontece (abortou antes)
        verify(notificationService, never()).create(anyLong(), any(), anyLong());
        assertEquals(TournamentStatus.ERROR, tournament.getStatus());
    }

    // ----------------------------------------------------------------

    private Tournament tournamentWith4Players() {
        Game game = new Game(1L, "bits", "jogo de teste");

        Tournament t = new Tournament();
        t.setId(TOURNAMENT_ID);
        t.setGame(game);
        t.setStatus(TournamentStatus.OPEN);
        t.setMaxParticipants(4);
        t.setCurrentParticipants(4);

        for (long i = 1; i <= 4; i++) {
            t.getUsers().add(new User(i, "Player " + i, "p" + i + "@test.com", "pw"));
        }
        return t;
    }
}