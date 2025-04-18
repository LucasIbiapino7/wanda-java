package com.cosmo.wanda_web.services.utils;

import com.cosmo.wanda_web.dto.match.DuelResponseDTO;
import com.cosmo.wanda_web.dto.tournament.BracketTournament;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

@Component
public class JsonConverter {
    public String converter(DuelResponseDTO data) {
        ObjectMapper mapper = new ObjectMapper();
        try{
            String jsonString = mapper.writeValueAsString(data);
            return jsonString;
        }catch (Exception e){
            System.out.println("erro ao ler o Json");
            return "";
        }
    }

    public String converterBracket(BracketTournament data){
        ObjectMapper mapper = new ObjectMapper();
        try {
            String jsonString = mapper.writeValueAsString(data);
            return jsonString;
        }catch (Exception e){
            System.out.println("erro ao ler o json");
            return "";
        }
    }

    public DuelResponseDTO converterToDto(String jsonString) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            DuelResponseDTO dto = mapper.readValue(jsonString, DuelResponseDTO.class);
            return dto;
        }catch (Exception e){
            return null;
        }
    }

    public BracketTournament converterToBracketDto(String jsonString){
        ObjectMapper mapper = new ObjectMapper();
        try {
            BracketTournament dto = mapper.readValue(jsonString, BracketTournament.class);
            return dto;
        }catch (Exception e){
            return null;
        }
    }
}
