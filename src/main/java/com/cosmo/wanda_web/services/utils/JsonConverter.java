package com.cosmo.wanda_web.services.utils;

import com.cosmo.wanda_web.dto.bits.DuelDTO;
import com.cosmo.wanda_web.dto.match.DuelResponseDTO;
import com.cosmo.wanda_web.dto.tournament.BracketTournament;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

@Component
public class JsonConverter {

    private final ObjectMapper mapper = new ObjectMapper();

    public String converter(DuelResponseDTO data) {
        try {
            return mapper.writeValueAsString(data);
        } catch (Exception e) {
            throw new IllegalStateException("Erro ao serializar DuelResponseDTO", e);
        }
    }

    public String converterBits(DuelDTO data) {
        try {
            return mapper.writeValueAsString(data);
        } catch (Exception e) {
            throw new IllegalStateException("Erro ao serializar DuelDTO (BITS)", e);
        }
    }

    public String converterBracket(BracketTournament data) {
        try {
            return mapper.writeValueAsString(data);
        } catch (Exception e) {
            throw new IllegalStateException("Erro ao serializar BracketTournament", e);
        }
    }

    public DuelResponseDTO converterToDuelResponseDto(String jsonString) {
        try {
            return mapper.readValue(jsonString, DuelResponseDTO.class);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("matchData não é compatível com DuelResponseDTO", e);
        }
    }

    public DuelDTO converterToBitsDuelDto(String jsonString) {
        try {
            return mapper.readValue(jsonString, DuelDTO.class);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("matchData não é compatível com DuelDTO (BITS)", e);
        }
    }

    public BracketTournament converterToBracketDto(String jsonString) {
        try {
            return mapper.readValue(jsonString, BracketTournament.class);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("JSON não é compatível com BracketTournament", e);
        }
    }
}
