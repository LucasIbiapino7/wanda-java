package com.cosmo.wanda_web.infra;

import com.cosmo.wanda_web.entities.SessionEvent;
import com.cosmo.wanda_web.entities.User;
import com.cosmo.wanda_web.repositories.SessionEventRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;

@Component
public class SessionEventInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(SessionEventInterceptor.class);

    private final SessionEventRepository sessionEventRepository;

    public SessionEventInterceptor(SessionEventRepository sessionEventRepository) {
        this.sessionEventRepository = sessionEventRepository;
    }

    // Mapa de METHOD:padrão-de-uri para label
    private static final Map<String, String> EVENT_MAP = Map.ofEntries(
            // Torneios
            Map.entry("POST:/tournament/subscribe","TORNEIO_INSCREVEU"),
            Map.entry("GET:/tournament/bracket","TORNEIO_BRACKET_VISUALIZOU"),
            // Partidas
            Map.entry("GET:/match/replay","PARTIDA_REPLAY_ASSISTIU"),
            // Funções
            Map.entry("PUT:/api/function","FUNCAO_SUBMETEU"),
            Map.entry("POST:/api/function/run", "FUNCAO_TESTOU"),
            Map.entry("POST:/api/function/feedback", "FUNCAO_FEEDBACK_PEDIU"),
            Map.entry("GET:/api/function", "FUNCAO_VISUALIZOU"),
            // Desafios
            Map.entry("POST:/challenge", "DESAFIO_CRIOU"),
            Map.entry("POST:/challenge/isAccepted", "DESAFIO_RESPONDEU"),
            // Perfil e ranking
            Map.entry("GET:/profile","PERFIL_VISUALIZOU"),
            Map.entry("PUT:/profile/nickname", "PERFIL_NICKNAME_ALTEROU"),
            Map.entry("PUT:/profile/character", "PERFIL_PERSONAGEM_ALTEROU")
    );

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof User user)) {
                return true;
            }

            String label = resolveLabel(request);
            if (label == null) {
                return true;
            }

            sessionEventRepository.save(new SessionEvent(user, label));
        } catch (Exception e) {
            log.warn("Falha ao salvar session event. erro={}", e.getMessage());
        }
        return true;
    }

    private String resolveLabel(HttpServletRequest request) {
        String method = request.getMethod();
        String uri = request.getRequestURI();

        String key = method + ":" + uri;
        if (EVENT_MAP.containsKey(key)) {
            return EVENT_MAP.get(key);
        }
        for (Map.Entry<String, String> entry : EVENT_MAP.entrySet()) {
            String[] parts = entry.getKey().split(":", 2);
            if (parts[0].equals(method) && uri.startsWith(parts[1] + "/")) {
                return entry.getValue();
            }
        }
        return null;
    }
}