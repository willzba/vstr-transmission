package com.vstr.video_chat.service;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Set;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        // Obtener los roles del usuario
        Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());

        if (roles.contains("ROLE_STREAMER")) {
            response.sendRedirect("/streamer/dashboard");
        } else if (roles.contains("ROLE_VIEWER")) {
            response.sendRedirect("/viewer/dashboard");
        } else {
            // Redirigir a una página por defecto si el rol no está definido
            response.sendRedirect("/default");
        }

    }
}
