package com.vstr.video_chat.controller;

import com.vstr.video_chat.model.Usuario;
import com.vstr.video_chat.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class Dashboard {
    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal) {
        // Obtiene el usuario autenticado
        String username = principal.getName();
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + username));

        // Verificar el rol del usuario
        if (usuario.getRoles().stream().anyMatch(role -> role.getNameRol().equals("ROLE_STREAMER"))) {
            return "redirect:/streamer/dashboard"; // Redirigir al dashboard del streamer
        } else if (usuario.getRoles().stream().anyMatch(role -> role.getNameRol().equals("ROLE_VIEWER"))) {
            return "redirect:/viewer/dashboard"; // Redirigir al dashboard del viewer
        }

        return "error"; // O una página de error si el rol no coincide
    }

}
