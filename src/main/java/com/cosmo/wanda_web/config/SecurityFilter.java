package com.cosmo.wanda_web.config;

import com.cosmo.wanda_web.infra.TokenService;
import com.cosmo.wanda_web.repositories.UserRepository;
import com.cosmo.wanda_web.services.AuthorizationService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(SecurityFilter.class);

    @Autowired
    private TokenService tokenService;

    @Autowired
    private AuthorizationService authorizationService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String token = recoverToken(request);
            if (token != null) {
                String login = tokenService.validateToken(token);
                if (login != null && !login.isBlank()) {
                    UserDetails user = authorizationService.loadUserByUsername(login);
                    var authentication = new UsernamePasswordAuthenticationToken(user, token, user.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    // Enriquece o MDC com dados do usuário autenticado
                    MDC.put("usuario_email", login);
                    MDC.put("usuario_perfil", user.getAuthorities().toString());
                }
            }
            filterChain.doFilter(request, response);
        } finally {
            MDC.clear();
        }
    }


    private String recoverToken(HttpServletRequest request){
        var authHeader = request.getHeader("Authorization");
        if (authHeader == null){
            return null; // sem token na requisição
        }
        return authHeader.replace("Bearer ", "");
    }

}
