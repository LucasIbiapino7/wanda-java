package com.cosmo.wanda_web.services.utils;

import com.cosmo.wanda_web.dto.match.MatchResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

@Component
public class JsonConverter {
    public String converter(MatchResponseDTO data) {
        ObjectMapper mapper = new ObjectMapper();
        try{
            String jsonString = mapper.writeValueAsString(data);
            return jsonString;
        }catch (Exception e){
            System.out.println("erro ao ler o Json");
            return "";
        }
    }

    public MatchResponseDTO converterToDto(String jsonString) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            MatchResponseDTO dto = mapper.readValue(jsonString, MatchResponseDTO.class);
            return dto;
        }catch (Exception e){
            return null;
        }
    }
}
