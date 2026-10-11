package com.cosmo.wanda_web.dto.tournament;

import com.cosmo.wanda_web.entities.User;

// Representa um jogador real ou fake no torneio
public record TournamentParticipantDTO (
    Long id,
    String name,
    boolean fake
){
    public static TournamentParticipantDTO fromUser(User user) {
        return new TournamentParticipantDTO(user.getId(), user.getName(), false);
    }

    // Jogadores fakes possuem ID negativo
    public static TournamentParticipantDTO fake (int num) {
        if (num < 1){
            throw new IllegalArgumentException(
                "O número do jogador fictício deve ser positivo."
            );
        }
        return new TournamentParticipantDTO(- (long) num, "Jogador fictício " + num, true);
    }
}
