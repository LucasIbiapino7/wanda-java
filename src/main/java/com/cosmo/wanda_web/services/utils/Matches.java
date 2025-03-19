package com.cosmo.wanda_web.services.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class Matches {
    private Long playerId1;
    private Long playerId2;
    private final List<String> cardsPlayer1 = new ArrayList<>();
    private final List<String> cardsPlayer2 = new ArrayList<>();
    private final List<String> parametersPlayer1 = new ArrayList<>();
    private final List<String> parametersPlayer2 = new ArrayList<>();
    private final List<String> cardsGame = new ArrayList<>(List.of("pedra","pedra", "tesoura", "tesoura", "papel", "papel"));
    private Integer rounds;
    private Integer player1RoundsVictories;
    private Integer player2RoundsVictories;
    private Integer tie; //empate

    public Matches() {
    }

    public Matches(Long playerId1, Long playerId2, Integer rounds) {
        this.playerId1 = playerId1;
        this.playerId2 = playerId2;
        this.rounds = rounds;
        this.player1RoundsVictories = 0;
        this.player2RoundsVictories = 0;
        this.tie = 0;
    }

    public Long getPlayerId1() {
        return playerId1;
    }

    public Long getPlayerId2() {
        return playerId2;
    }

    public List<String> getCardsPlayer1() {
        return cardsPlayer1;
    }

    public List<String> getCardsPlayer2() {
        return cardsPlayer2;
    }

    public Integer getRounds() {
        return rounds;
    }

    public Integer getPlayer1RoundsVictories() {
        return player1RoundsVictories;
    }

    public Integer getPlayer2RoundsVictories() {
        return player2RoundsVictories;
    }

    public List<String> getParametersPlayer1() {
        return parametersPlayer1;
    }

    public List<String> getParametersPlayer2() {
        return parametersPlayer2;
    }

    public Integer getTie() {
        return tie;
    }

    /***
     * Intancia as Listas que representam as cartas de cada jogador no começo do turno
     * As cartas são selecionadas aleatoriamente, podendo um jogador ter 2x a mesma carta
     */
    public void instanceCards(){
        Random random = new Random();
        cardsPlayer1.clear();

        // Adicionar as cartas do player 1 para um ROUND
        for (int i = 0; i < 3; i++){
            int index = random.nextInt(cardsGame.size());
            cardsPlayer1.add(cardsGame.remove(index));
        }

        System.out.println("Cartas do jogador 1:" + cardsPlayer1);

        cardsPlayer2.clear();

        while (!cardsGame.isEmpty()){
            cardsPlayer2.add(cardsGame.remove(0));
        }

        System.out.println("Cartas do jogador 2:" + cardsPlayer2);

        cardsGame.add("pedra");
        cardsGame.add("pedra");
        cardsGame.add("papel");
        cardsGame.add("papel");
        cardsGame.add("tesoura");
        cardsGame.add("tesoura");
    }

    /***
     * Funcao responsavel por atualizar as cartas após elas serem usadas no turno
     * @param cardsPlayer - Lista de cartas do jogador
     * @param cardTurnPlayer1 - carta utilizada no turno
     */
    public void updateCardsPlayer(List<String> cardsPlayer, String cardTurnPlayer1) {
        for (int i = 0; i < cardsPlayer.size(); i++){
            if (cardsPlayer.get(i) != null){
                if (cardsPlayer.get(i).equalsIgnoreCase(cardTurnPlayer1)) {
                    cardsPlayer.set(i, null); // Substitui o valor por null
                    break; // Sai do loop após a substituição
                }
            }
        }
    }

    // Apagar depois
    public void updateCardsPlayer1(String cardTurnPlayer1) {
        for (int i = 0; i < cardsPlayer1.size(); i++){
            if (cardsPlayer1.get(i) != null){
                if (cardsPlayer1.get(i).equalsIgnoreCase(cardTurnPlayer1)) {
                    cardsPlayer1.set(i, null); // Substitui o valor por null
                    break; // Sai do loop após a substituição
                }
            }
        }
    }

    // Apagar depois
    public void updateCardsPlayer2(String cardTurnPlayer2) {
        for (int i = 0; i < cardsPlayer2.size(); i++){
            if (cardsPlayer2.get(i) != null){
                if (cardsPlayer2.get(i).equalsIgnoreCase(cardTurnPlayer2)) {
                    cardsPlayer2.set(i, null); // Substitui o valor por null
                    break; // Sai do loop após a substituição
                }
            }
        }
    }

    /***
     * Válida se a carta recebida é válida, ou seja, se ainda está na mão do jogaror
     * No caso de um retorno inválido ou carta que não está na mão, retornamos a próxima carta da mão do jogador
     * @param cardsPlayer - lista de cartas dp jogador
     * @param player1Choice - carta escolhida pela funcao
     * @return - se a carta existir, retorna ela, caso não, retorna a primeira da mão
     */
    public String validateCardPlayer(List<String> cardsPlayer, String player1Choice) {
        //Verifica se a carta ainda está na mão e retorna se estiver (coloca null na carta)
        for (String card : cardsPlayer) {
            if (player1Choice.equalsIgnoreCase(card)){
                return card;
            }
        }
        // Caso seja uma carta inválida ou um retorno inválido, retorna a primeira não Null
        for (String card : cardsPlayer) {
            if (card != null){
                return card;
            }
        }
        return "";
    }

    /***
     * Funcao que atualiza os parametros para serem enviados
     * @param parametersPlayer - lista que representa os parametros do jogador
     */
    public void updateParametersPlayer(List<String> parametersPlayer){
        parametersPlayer.clear();
        if (parametersPlayer == parametersPlayer1){
            for (String card : cardsPlayer1) {
                parametersPlayer.add(card);
            }
            for (String card : cardsPlayer2) {
                parametersPlayer.add(card);
            }
        } else if (parametersPlayer == parametersPlayer2) {
            for (String card : cardsPlayer2) {
                parametersPlayer.add(card);
            }
            for (String card : cardsPlayer1) {
                parametersPlayer.add(card);
            }
        }
    }


    /***
     *
     * @param player1Choice - escolha do player 1
     * @param player2Choice - escolha do player 2
     * @return - 0 - empate, 1 - jogador 1 venceu, 2 - jogador 2 venceu
     */
    public Integer conflict(String player1Choice, String player2Choice) {
        if (player1Choice.equalsIgnoreCase("papel")){
            if (player2Choice.equalsIgnoreCase("papel")){
                return 0;
            } else if (player2Choice.equalsIgnoreCase("tesoura")) {
                return 2;
            } else if (player2Choice.equalsIgnoreCase("pedra")){
                return 1;
            }
        } else if (player1Choice.equalsIgnoreCase("tesoura")) {
            if (player2Choice.equalsIgnoreCase("papel")){
                return 1;
            } else if (player2Choice.equalsIgnoreCase("tesoura")) {
                return 0;
            } else if (player2Choice.equalsIgnoreCase("pedra")){
                return 2;
            }
        } else { // Pedra
            if (player2Choice.equalsIgnoreCase("papel")){
                return 2;
            } else if (player2Choice.equalsIgnoreCase("tesoura")) {
                return 1;
            } else if (player2Choice.equalsIgnoreCase("pedra")){
                return 0;
            }
        }
        return -1; // Lembrar de mudar isso;
    }

    public void roundWinner(RoundInformation turnInfo, CurrentScore score) {
        if (turnInfo.getPlayer1TurnWins() == turnInfo.getPlayer2TurnWins()){
            tie++;
            score.tie();
        } else if (turnInfo.getPlayer1TurnWins() > turnInfo.getPlayer2TurnWins()) {
            player1RoundsVictories++;
            score.player1win();
        }else {
            player2RoundsVictories++;
            score.player2win();
        }
    }

    public boolean victory() {
        int minVictories = rounds / 2;
        return player1RoundsVictories > minVictories || player2RoundsVictories > minVictories || Objects.equals(tie, rounds);
    }

    @Override
    public String toString() {
        return "Matches{" +
                "playerId1=" + playerId1 +
                ", playerId2=" + playerId2 +
                ", cardsPlayer1=" + cardsPlayer1 +
                ", cardsPlayer2=" + cardsPlayer2 +
                ", parametersPlayer1=" + parametersPlayer1 +
                ", parametersPlayer2=" + parametersPlayer2 +
                ", rounds=" + rounds +
                ", player1RoundsVictories=" + player1RoundsVictories +
                ", player2RoundsVictories=" + player2RoundsVictories +
                '}';
    }
}
