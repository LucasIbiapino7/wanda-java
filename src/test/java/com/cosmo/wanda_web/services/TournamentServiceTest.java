package com.cosmo.wanda_web.services;

import com.cosmo.wanda_web.dto.tournament.TournamentMinDTO;
import com.cosmo.wanda_web.dto.tournament.TournamentUpdateDTO;
import com.cosmo.wanda_web.dto.tournament.TournamentWithParticipantsDTO;
import com.cosmo.wanda_web.entities.*;
import com.cosmo.wanda_web.repositories.TournamentRepository;
import com.cosmo.wanda_web.services.exceptions.ResourceNotFoundException;
import com.cosmo.wanda_web.services.exceptions.TournamentException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TournamentServiceTest {

    @Mock
    private TournamentRepository tournamentRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private TournamentService tournamentService;

    private User criador;
    private User outroUsuario;
    private Tournament torneio;

    @BeforeEach
    void setUp() {
        criador = new User();
        criador.setId(1L);
        criador.setName("Criador");
        criador.setEmail("criador@test.com");
        criador.setPassword("senha");
        criador.setProfileType(ProfileType.STUDENT);

        Player playerCriador = new Player();
        playerCriador.setCharacterUrl("avatar.png");
        playerCriador.setNickname("criador_nick");
        playerCriador.setNumberOfMatches(0);
        playerCriador.setNumberOfWinners(0);
        playerCriador.setWinsTournaments(0);
        criador.setPlayer(playerCriador);

        outroUsuario = new User();
        outroUsuario.setId(2L);
        outroUsuario.setName("Outro");
        outroUsuario.setEmail("outro@test.com");
        outroUsuario.setPassword("senha");
        outroUsuario.setProfileType(ProfileType.STUDENT);

        Game jogo = new Game(1L, "jokenpo", "Teste");

        torneio = new Tournament();
        torneio.setId(10L);
        torneio.setName("Torneio Teste");
        torneio.setDescription("Descrição teste");
        torneio.setCreatedAt(LocalDateTime.now());
        torneio.setStartTime(LocalDateTime.now().plusHours(2));
        torneio.setMaxParticipants(8);
        torneio.setCurrentParticipants(3);
        torneio.setStatus(TournamentStatus.OPEN);
        torneio.setAsPrivate(false);
        torneio.setCreator(criador);
        torneio.setGame(jogo);
    }

    // ------------------------------------------------------------------
    // update
    // ------------------------------------------------------------------

    @Test
    void update_deveAtualizarComSucesso() {
        TournamentUpdateDTO dto = new TournamentUpdateDTO();
        dto.setName("Novo Nome");
        dto.setDescription("Nova descrição");
        dto.setStartTime(LocalDateTime.now().plusHours(3));

        when(tournamentRepository.findById(10L)).thenReturn(Optional.of(torneio));
        when(userService.authenticated()).thenReturn(criador);
        when(tournamentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        TournamentMinDTO result = tournamentService.update(10L, dto);

        assertThat(result.getName()).isEqualTo("Novo Nome");
        assertThat(result.getDescription()).isEqualTo("Nova descrição");
        verify(tournamentRepository).save(torneio);
    }

    @Test
    void update_deveLancarExcecaoQuandoTorneioNaoEncontrado() {
        when(tournamentRepository.findById(99L)).thenReturn(Optional.empty());

        TournamentUpdateDTO dto = new TournamentUpdateDTO();
        dto.setName("Nome");
        dto.setDescription("Desc");

        assertThatThrownBy(() -> tournamentService.update(99L, dto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Torneio não encontrado");
    }

    @Test
    void update_deveLancarExcecaoQuandoUsuarioNaoEhCriador() {
        when(tournamentRepository.findById(10L)).thenReturn(Optional.of(torneio));
        when(userService.authenticated()).thenReturn(outroUsuario);

        TournamentUpdateDTO dto = new TournamentUpdateDTO();
        dto.setName("Nome");
        dto.setDescription("Desc");

        assertThatThrownBy(() -> tournamentService.update(10L, dto))
                .isInstanceOf(TournamentException.class)
                .hasMessageContaining("criador");
    }

    @Test
    void update_deveLancarExcecaoQuandoStatusNaoEhOpen() {
        torneio.setStatus(TournamentStatus.FINISHED);

        when(tournamentRepository.findById(10L)).thenReturn(Optional.of(torneio));
        when(userService.authenticated()).thenReturn(criador);

        TournamentUpdateDTO dto = new TournamentUpdateDTO();
        dto.setName("Nome");
        dto.setDescription("Desc");

        assertThatThrownBy(() -> tournamentService.update(10L, dto))
                .isInstanceOf(TournamentException.class)
                .hasMessageContaining("OPEN");
    }

    @Test
    void update_deveLancarExcecaoQuandoStartTimeInvalido() {
        when(tournamentRepository.findById(10L)).thenReturn(Optional.of(torneio));
        when(userService.authenticated()).thenReturn(criador);

        TournamentUpdateDTO dto = new TournamentUpdateDTO();
        dto.setName("Nome");
        dto.setDescription("Desc");
        dto.setStartTime(LocalDateTime.now().plusMinutes(5)); // menos de 15 min

        assertThatThrownBy(() -> tournamentService.update(10L, dto))
                .isInstanceOf(TournamentException.class)
                .hasMessageContaining("Start Time");
    }

    // ------------------------------------------------------------------
    // cancel
    // ------------------------------------------------------------------

    @Test
    void cancel_deveCancelarComSucesso() {
        when(tournamentRepository.findById(10L)).thenReturn(Optional.of(torneio));
        when(userService.authenticated()).thenReturn(criador);
        when(tournamentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        tournamentService.cancel(10L);

        assertThat(torneio.getStatus()).isEqualTo(TournamentStatus.CANCELLED);
        verify(tournamentRepository).save(torneio);
    }

    @Test
    void cancel_deveLancarExcecaoQuandoTorneioNaoEncontrado() {
        when(tournamentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tournamentService.cancel(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Torneio não encontrado");
    }

    @Test
    void cancel_deveLancarExcecaoQuandoUsuarioNaoEhCriador() {
        when(tournamentRepository.findById(10L)).thenReturn(Optional.of(torneio));
        when(userService.authenticated()).thenReturn(outroUsuario);

        assertThatThrownBy(() -> tournamentService.cancel(10L))
                .isInstanceOf(TournamentException.class)
                .hasMessageContaining("criador");
    }

    @Test
    void cancel_deveLancarExcecaoQuandoStatusNaoEhOpen() {
        torneio.setStatus(TournamentStatus.RUNNING);

        when(tournamentRepository.findById(10L)).thenReturn(Optional.of(torneio));
        when(userService.authenticated()).thenReturn(criador);

        assertThatThrownBy(() -> tournamentService.cancel(10L))
                .isInstanceOf(TournamentException.class)
                .hasMessageContaining("OPEN");
    }

    // ------------------------------------------------------------------
    // findByIdWithParticipants
    // ------------------------------------------------------------------

    @Test
    void findByIdWithParticipants_deveRetornarParticipantes() {
        when(tournamentRepository.findByIdWithParticipants(10L)).thenReturn(torneio);

        TournamentWithParticipantsDTO result = tournamentService.findByIdWithParticipants(10L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(10L);
    }

    @Test
    void findByIdWithParticipants_deveLancarExcecaoQuandoTorneioNaoEncontrado() {
        when(tournamentRepository.findByIdWithParticipants(99L)).thenReturn(null);

        assertThatThrownBy(() -> tournamentService.findByIdWithParticipants(99L))
                .isInstanceOf(TournamentException.class)
                .hasMessageContaining("não existe");
    }
}