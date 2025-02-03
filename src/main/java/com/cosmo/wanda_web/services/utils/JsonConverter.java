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
            System.out.println(jsonString);
            return jsonString;
        }catch (Exception e){
            System.out.println("erro ao ler o Json");
            return "";
        }
    }
}
