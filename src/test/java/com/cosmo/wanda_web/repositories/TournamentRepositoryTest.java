package com.cosmo.wanda_web.repositories;

import com.cosmo.wanda_web.entities.Game;
import com.cosmo.wanda_web.entities.ProfileType;
import com.cosmo.wanda_web.entities.Tournament;
import com.cosmo.wanda_web.entities.TournamentStatus;
import com.cosmo.wanda_web.entities.User;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class TournamentRepositoryTest {

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private EntityManager entityManager;

    private Tournament torneio;

    @BeforeEach
    void setUp() {
        User criador = new User();
        criador.setName("Criador");
        criador.setEmail("criador@test.com");
        criador.setPassword("senha123");
        criador.setProfileType(ProfileType.STUDENT);
        userRepository.save(criador);

        Game jogo = gameRepository.findByNameIgnoreCase("jokenpo")
                .orElseGet(() -> {
                    Game g = new Game();
                    g.setName("jokenpo");
                    g.setDescription("Teste");
                    return gameRepository.save(g);
                });

        torneio = new Tournament();
        torneio.setName("Torneio Teste");
        torneio.setDescription("Desc");
        torneio.setCreatedAt(LocalDateTime.now());
        torneio.setStartTime(LocalDateTime.now().plusHours(1));
        torneio.setMaxParticipants(8);
        torneio.setCurrentParticipants(7);
        torneio.setStatus(TournamentStatus.OPEN);
        torneio.setAsPrivate(false);
        torneio.setCreator(criador);
        torneio.setGame(jogo);
        torneio = tournamentRepository.save(torneio);
    }

    @Test
    void trySubscribe_deveRetornarZeroQuandoTorneioPlenoDesdeOInicio() {
        torneio.setCurrentParticipants(8);
        tournamentRepository.save(torneio);

        int resultado = tournamentRepository.trySubscribe(torneio.getId());

        assertThat(resultado).isEqualTo(0);
    }

    @Test
    void trySubscribe_deveRetornarZeroQuandoTorneioNaoEstaAberto() {
        torneio.setStatus(TournamentStatus.RUNNING);
        tournamentRepository.save(torneio);

        int resultado = tournamentRepository.trySubscribe(torneio.getId());

        assertThat(resultado).isEqualTo(0);
    }

    @Test
    void trySubscribe_deveIncrementarCurrentParticipantsQuandoHaVaga() {
        int antes = torneio.getCurrentParticipants(); // 7

        int resultado = tournamentRepository.trySubscribe(torneio.getId());

        // @Modifying não invalida o cache L1 — flush+clear forçam releitura do banco
        entityManager.flush();
        entityManager.clear();

        Tournament atualizado = tournamentRepository.findById(torneio.getId()).orElseThrow();
        assertThat(resultado).isEqualTo(1);
        assertThat(atualizado.getCurrentParticipants()).isEqualTo(antes + 1);
    }
}