package com.cosmo.wanda_web.infra;

import io.opentelemetry.api.trace.Span;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;

@Component
public class TelegramNotifier {

    private static final Logger log = LoggerFactory.getLogger(TelegramNotifier.class);

    @Value("${telegram.bot.token:}")
    private String botToken;

    @Value("${telegram.chat.id:}")
    private String chatId;

    private final RestTemplate restTemplate = new RestTemplate();

    public void send(String message) {
        if (botToken.isBlank() || chatId.isBlank()) {
            return;
        }
        try {
            String url = String.format(
                    "https://api.telegram.org/bot%s/sendMessage", botToken
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            String body = String.format(
                    "{\"chat_id\": \"%s\", \"text\": \"%s\"}",
                    chatId,
                    message.replace("\"", "\\\"").replace("\n", "\\n")
            );

            HttpEntity<String> entity = new HttpEntity<>(body, headers);
            restTemplate.postForObject(url, entity, String.class);
        } catch (Exception e) {
            log.warn("Falha ao enviar alerta pro Telegram. erro={}", e.getMessage());
        }
    }

    public void sendError(Exception e, HttpServletRequest request) {
        String traceId = resolveTraceId();

        // lê usuário do MDC
        String usuario = org.slf4j.MDC.get("usuario_email");
        if (usuario == null) usuario = "não autenticado";

        // extrai causa raiz e body do Python se for FeignException
        String tipo = e.getClass().getSimpleName();
        String mensagem = e.getMessage() != null ? e.getMessage() : "sem mensagem";
        String respostaPython = "";

        if (e instanceof feign.FeignException fe) {
            tipo = fe.getClass().getSimpleName();
            String body = fe.contentUTF8();
            if (body != null && !body.isBlank()) {
                respostaPython = "\nResposta Python: " + body;
            }
            if (fe.getCause() != null) {
                mensagem = fe.getCause().getMessage() != null ? fe.getCause().getMessage() : mensagem;
            }
        } else if (e.getCause() != null && e.getCause().getMessage() != null) {
            mensagem = e.getCause().getMessage();
        }

        String message = String.format(
                "🚨 Erro no Wanda\n" +
                        "Tipo: %s\n" +
                        "URI: %s %s\n" +
                        "Usuário: %s\n" +
                        "Mensagem: %s%s\n" +
                        "Trace ID: %s\n" +
                        "Momento: %s",
                tipo, request.getMethod(), request.getRequestURI(), usuario, mensagem, respostaPython, traceId, Instant.now()
        );

        send(message);
    }

    private String resolveTraceId() {
        try {
            Span span = Span.current();
            if (span != null && span.getSpanContext().isValid()) {
                return span.getSpanContext().getTraceId();
            }
        } catch (Exception ex) {
            log.warn("Não foi possível obter o trace ID. erro={}", ex.getMessage());
        }
        return "indisponível";
    }
}