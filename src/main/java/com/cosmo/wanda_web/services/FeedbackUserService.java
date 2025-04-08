package com.cosmo.wanda_web.services;

import com.cosmo.wanda_web.dto.users.FeedbackUserDTO;
import com.cosmo.wanda_web.entities.LogAnswersAgents;
import com.cosmo.wanda_web.repositories.LogAnswersAgentsRepository;
import com.cosmo.wanda_web.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FeedbackUserService {

    @Autowired
    private LogAnswersAgentsRepository logAnswersAgentsRepository;

    @Transactional
    public void feedbackByUser(FeedbackUserDTO dto) {
        LogAnswersAgents logAnswersAgents = logAnswersAgentsRepository.findById(dto.getFeedbackId()).orElseThrow(
                () -> new ResourceNotFoundException("Recurso não encontrado!"));
        logAnswersAgents.setFeedbackUser(dto.getFeedbackUser());
        logAnswersAgentsRepository.save(logAnswersAgents);
    }
}
