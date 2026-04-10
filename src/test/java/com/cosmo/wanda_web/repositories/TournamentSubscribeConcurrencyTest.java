package com.cosmo.wanda_web.repositories;

import com.cosmo.wanda_web.entities.Game;
import com.cosmo.wanda_web.entities.ProfileType;
import com.cosmo.wanda_web.entities.Tournament;
import com.cosmo.wanda_web.entities.TournamentStatus;
import com.cosmo.wanda_web.entities.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Teste de concorrência isolado do TournamentRepositoryTest.
 *
 * Precisa estar em classe separada porque usa @Commit no setUp,
 * o que conflita com o rollback automático do @DataJpaTest nos demais testes.
 * Aqui o @AfterEach faz a limpeza manual no banco.
 */
@DataJpaTest
@ActiveProfiles("test")
@Transactional // cada método tem sua transação — exceto os anotados com NOT_SUPPORTED
class TournamentSubscribeConcurrencyTest {

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private TransactionTemplate transactionTemplate;

    private Long torneioId;
    private Long criadorId;

    /**
     * Commita os dados no banco para que as threads filhas consigam enxergá-los.
     * Sem @Commit, o H2 manteria os dados só na transação do teste, invisíveis
     * para outras conexões/threads.
     */
    @BeforeEach
    @Commit
    void setUp() {
        User criador = new User();
        criador.setName("Criador Concorrencia");
        criador.setEmail("criador-concorrencia@test.com");
        criador.setPassword("senha123");
        criador.setProfileType(ProfileType.STUDENT);
        userRepository.save(criador);
        criadorId = criador.getId();

        Game jogo = gameRepository.findByNameIgnoreCase("jokenpo")
                .orElseGet(() -> {
                    Game g = new Game();
                    g.setName("jokenpo");
                    g.setDescription("Teste");
                    return gameRepository.save(g);
                });

        Tournament torneio = new Tournament();
        torneio.setName("Torneio Concorrencia");
        torneio.setDescription("Desc");
        torneio.setCreatedAt(LocalDateTime.now());
        torneio.setStartTime(LocalDateTime.now().plusHours(1));
        torneio.setMaxParticipants(8);
        torneio.setCurrentParticipants(7); // 1 vaga restante
        torneio.setStatus(TournamentStatus.OPEN);
        torneio.setAsPrivate(false);
        torneio.setCreator(criador);
        torneio.setGame(jogo);
        tournamentRepository.save(torneio);
        torneioId = torneio.getId();
    }

    /**
     * Limpeza manual — necessária porque o @Commit no setUp persiste os dados
     * de verdade, então o rollback automático do @DataJpaTest não os remove.
     */
    @AfterEach
    @Commit
    void tearDown() {
        tournamentRepository.deleteById(torneioId);
        userRepository.deleteById(criadorId);
    }

    @Test
    @Transactional(propagation = org.springframework.transaction.annotation.Propagation.NOT_SUPPORTED)
    void trySubscribe_devePermitirApenasUmaInscricaoQuandoDuasThreadsDisputamAUltimaVaga()
            throws InterruptedException {

        int numeroDeThreads = 2;
        ExecutorService executor = Executors.newFixedThreadPool(numeroDeThreads);

        // CyclicBarrier garante que as duas threads disparam ao mesmo tempo,
        // maximizando a chance de colisão real no banco
        CyclicBarrier barreira = new CyclicBarrier(numeroDeThreads);

        AtomicInteger inscricoesRealizadas = new AtomicInteger(0);
        AtomicInteger inscricoesBarradas = new AtomicInteger(0);

        List<Future<?>> futures = new ArrayList<>();

        for (int i = 0; i < numeroDeThreads; i++) {
            futures.add(executor.submit(() -> {
                try {
                    barreira.await();
                    // Cada thread executa em sua própria transação,
                    // simulando dois requests chegando ao mesmo tempo
                    Integer linhasAfetadas = transactionTemplate.execute(
                            status -> tournamentRepository.trySubscribe(torneioId)
                    );
                    if (linhasAfetadas != null && linhasAfetadas == 1) {
                        inscricoesRealizadas.incrementAndGet();
                    } else {
                        inscricoesBarradas.incrementAndGet();
                    }
                } catch (Exception e) {
                    inscricoesBarradas.incrementAndGet();
                }
            }));
        }

        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);

        assertThat(inscricoesRealizadas.get())
                .as("Exatamente 1 inscrição deve ter sido realizada")
                .isEqualTo(1);

        assertThat(inscricoesBarradas.get())
                .as("Exatamente 1 inscrição deve ter sido barrada")
                .isEqualTo(1);

        Tournament atualizado = tournamentRepository.findById(torneioId).orElseThrow();
        assertThat(atualizado.getCurrentParticipants())
                .as("currentParticipants não pode ultrapassar maxParticipants")
                .isLessThanOrEqualTo(atualizado.getMaxParticipants());
    }
}