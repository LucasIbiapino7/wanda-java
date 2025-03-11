package com.cosmo.wanda_web.services;

import com.cosmo.wanda_web.dto.challengers.ChallengeDTO;
import com.cosmo.wanda_web.entities.Challenge;
import com.cosmo.wanda_web.entities.ChallengeStatus;
import com.cosmo.wanda_web.entities.User;
import com.cosmo.wanda_web.repositories.ChallengeRepository;
import com.cosmo.wanda_web.repositories.UserRepository;
import com.cosmo.wanda_web.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Service
public class ChallengeService {

    @Autowired
    private ChallengeRepository challengeRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public void challenge(ChallengeDTO dto) {
        User userChallenger = userService.authenticated(); // Usuário Logado

        User userChallenged = userRepository.findById(dto.getChallengedId()).orElseThrow(
                () -> new ResourceNotFoundException("Usuário desafiado não foi encontrado"));

        System.out.println(userChallenger.getId());
        System.out.println(userChallenged.getId());

        // Verificar se há um desafio pendente entre esses dois alunos
        if (challengeRepository.checkIfChallengePendingExists(userChallenger.getId(), userChallenged.getId()).isPresent()){
            throw new ResourceNotFoundException("Já existe um desafio pendente!"); // Mudar essa exceção
        }

        Challenge challenge = new Challenge();
        challenge.setChallenger(userChallenger);
        challenge.setChallenged(userChallenged);
        challenge.setCreatedAt(Instant.now());
        challenge.setMatch(null);
        challenge.setStatus(ChallengeStatus.PENDING);

        challengeRepository.save(challenge);
    }
}
