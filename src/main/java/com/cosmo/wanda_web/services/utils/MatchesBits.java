package com.cosmo.wanda_web.services.utils;

import com.cosmo.wanda_web.dto.bits.MatchDecisionBitsDTO;
import com.cosmo.wanda_web.dto.bits.PlaysBitsDTO;
import com.cosmo.wanda_web.utils.BitsCard;
import com.cosmo.wanda_web.utils.ParametersBits;
import jakarta.validation.constraints.Null;

import java.util.*;

public class MatchesBits {
    private Long playerId1;
    private Long playerId2;
    private Map<String, Object> cardsPlayer1 = new LinkedHashMap<>();
    private Map<String, Object> cardsPlayer2 = new LinkedHashMap<>();
    public final static List<String> ORDEM_MAO = new ArrayList<>(List.of("BIT8", "BIT16", "BIT32", "FIREWALL"));
    private Integer matches;
    private Integer player1RoundsVictories = 0;
    private Integer player2RoundsVictories = 0;
    private Integer tie = 0;

    public MatchesBits() {
    }

    public MatchesBits(Long playerId1, Long playerId2, Integer matches) {
        this.playerId1 = playerId1;
        this.playerId2 = playerId2;
        this.matches = matches;
    }

    public Long getPlayerId1() {
        return playerId1;
    }

    public void setPlayerId1(Long playerId1) {
        this.playerId1 = playerId1;
    }

    public Long getPlayerId2() {
        return playerId2;
    }

    public void setPlayerId2(Long playerId2) {
        this.playerId2 = playerId2;
    }

    public Map<String, Object> getCardsPlayer1() {
        return cardsPlayer1;
    }

    public Map<String, Object> getCardsPlayer2() {
        return cardsPlayer2;
    }

    public List<Object> getParamsPlayer1(){
        return cardsPlayer1.values().stream().toList();
    }

    public List<Object> getParamsPlayer2(){
        return cardsPlayer2.values().stream().toList();
    }

    public Integer getMatches() {
        return matches;
    }

    public void setMatches(Integer matches) {
        this.matches = matches;
    }

    public Integer getPlayer1RoundsVictories() {
        return player1RoundsVictories;
    }

    public void setPlayer1RoundsVictories(Integer player1RoundsVictories) {
        this.player1RoundsVictories = player1RoundsVictories;
    }

    public Integer getPlayer2RoundsVictories() {
        return player2RoundsVictories;
    }

    public void setPlayer2RoundsVictories(Integer player2RoundsVictories) {
        this.player2RoundsVictories = player2RoundsVictories;
    }

    public Integer getTie() {
        return tie;
    }

    public void setTie(Integer tie) {
        this.tie = tie;
    }

    /**
     * Instancia as cartas inicias de uma partida
     * bit8, bit16, bit32 e firewall com 1
     * opp_last - null
     */
    public void instanceInitialCards() {
        cardsPlayer1.clear();
        cardsPlayer1.put("BIT8", 1);
        cardsPlayer1.put("BIT16", 1);
        cardsPlayer1.put("BIT32", 1);
        cardsPlayer1.put("FIREWALL", 1);
        cardsPlayer1.put("OPP_LAST", null);

        cardsPlayer2.clear();
        cardsPlayer2.put("BIT8", 1);
        cardsPlayer2.put("BIT16", 1);
        cardsPlayer2.put("BIT32", 1);
        cardsPlayer2.put("FIREWALL", 1);
        cardsPlayer2.put("OPP_LAST", null);
    }

    /**
     * Valida o retorna da requisicao
     * Verificando se o retorno é um dos valores das cartas do jogo e se o jogador tem aquela carta
     * Caso nao tenha, eh escolhida a proxima carta da mao, desde que o jogador tenha a carta
     */
    public String validateChoice(Map<String, Object> cardsPlayer1, String player1Choice) {
        int countOrdemMao = 0;
        String choice = player1Choice;
        while (true){
            if (cardsPlayer1.containsKey(choice)){
                if ((Integer) cardsPlayer1.get(choice) > 0){
                    return choice;
                }
                if (countOrdemMao <= ORDEM_MAO.size()){
                    choice = ORDEM_MAO.get(countOrdemMao);
                    countOrdemMao++;
                }
            }else {
                choice = ORDEM_MAO.get(countOrdemMao);
                countOrdemMao++;
            }
        }
    }

    /**
     * Verifica o vencedor do round de acordo com as regras do jogo
     * 1 - player 1 venceu
     * 2 - player 2 venceu
     * 0 - empate
     */
    public Integer conflict(String cardPlayer1, String cardPlayer2) {
        if (cardPlayer1.equalsIgnoreCase("BIT8")){
            if (cardPlayer2.equalsIgnoreCase("BIT8")){
                return 0;
            } else if (cardPlayer2.equalsIgnoreCase("BIT16")) {
                return 2;
            } else if (cardPlayer2.equalsIgnoreCase("BIT32")) {
                return 2;
            } else if (cardPlayer2.equalsIgnoreCase("FIREWALL")) {
                return 2;
            }
        } else if (cardPlayer1.equalsIgnoreCase("BIT16")) {
            if (cardPlayer2.equalsIgnoreCase("BIT8")){
                return 1;
            } else if (cardPlayer2.equalsIgnoreCase("BIT16")) {
                return 0;
            } else if (cardPlayer2.equalsIgnoreCase("BIT32")) {
                return 2;
            } else if (cardPlayer2.equalsIgnoreCase("FIREWALL")) {
                return 2;
            }
        } else if (cardPlayer1.equalsIgnoreCase("BIT32")) {
            if (cardPlayer2.equalsIgnoreCase("BIT8")){
                return 1;
            } else if (cardPlayer2.equalsIgnoreCase("BIT16")) {
                return 1;
            } else if (cardPlayer2.equalsIgnoreCase("BIT32")) {
                return 0;
            } else if (cardPlayer2.equalsIgnoreCase("FIREWALL")) {
                return 2;
            }
        } else if (cardPlayer1.equalsIgnoreCase("FIREWALL")) {
            if (cardPlayer2.equalsIgnoreCase("BIT8")){
                return 1;
            } else if (cardPlayer2.equalsIgnoreCase("BIT16")) {
                return 1;
            } else if (cardPlayer2.equalsIgnoreCase("BIT32")) {
                return 1;
            } else if (cardPlayer2.equalsIgnoreCase("FIREWALL")) {
                return 0;
            }
        }
        return null;
    }

    /**
     * Atualiza as cartas depois de um Round
     * Muda o valor da carta jogada para 0
     * e seta o valor de OPP_LAST para a carta usada pelo outro jogador
     */
    public void updatePlayerCards(String cardPlayer1, String cardPlayer2) {
        if (cardsPlayer1.containsKey(cardPlayer1)){
            cardsPlayer1.put(cardPlayer1, 0);
            cardsPlayer2.put("OPP_LAST", cardPlayer1);
        }
        if (cardsPlayer2.containsKey(cardPlayer2)){
            cardsPlayer2.put(cardPlayer2, 0);
            cardsPlayer1.put("OPP_LAST", cardPlayer2);
        }
    }

    public MatchDecisionBitsDTO roundWinner(RoundInformation roundInformation, CurrentScore currentScore, List<PlaysBitsDTO> plays) {
        int p1 = roundInformation.getPlayer1TurnWins();
        int p2 = roundInformation.getPlayer2TurnWins();
        MatchDecisionBitsDTO decision = new MatchDecisionBitsDTO();
        if (p1 > p2) {
            player1RoundsVictories++;
            currentScore.player1win();
            decision.setOfficialWinner(1);
            decision.setDecidedBy("BY_ROUNDS");
            return decision;
        }

        if (p2 > p1) {
            player2RoundsVictories++;
            currentScore.player2win();
            decision.setOfficialWinner(2);
            decision.setDecidedBy("BY_ROUNDS");
            return decision;
        }

        Integer lastNonTieWinner = getLastNonTieWinner(plays);

        if (lastNonTieWinner != null) {
            if (lastNonTieWinner == 1) {
                player1RoundsVictories++;
                currentScore.player1win();
            } else {
                player2RoundsVictories++;
                currentScore.player2win();
            }
            decision.setOfficialWinner(lastNonTieWinner);
            decision.setDecidedBy("TIE_BREAK_LAST_NON_TIE_ROUND");
        } else {
            player1RoundsVictories++;
            currentScore.player1win();
            decision.setOfficialWinner(1);
            decision.setDecidedBy("TIE_BREAK_FALLBACK");
        }

        return decision;
    }

    private Integer getLastNonTieWinner(List<PlaysBitsDTO> plays) {
        for (int i = plays.size() - 1; i >= 0; i--) {
            PlaysBitsDTO play = plays.get(i);
            int winnerOfPlay = play.getResolved().getWinnerOfPlay();

            if (winnerOfPlay == 1 || winnerOfPlay == 2) {
                return winnerOfPlay;
            }
        }
        return null;
    }


    public boolean endMatch(int countMatch) {
        if (countMatch > this.matches){
            if (player1RoundsVictories > player2RoundsVictories) return false;
            if (player2RoundsVictories > player1RoundsVictories) return false;
            if (countMatch > (matches + 5)) return false;
            return true;
        }
        return true;
    }
}
