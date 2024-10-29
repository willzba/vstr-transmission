package com.vstr.video_chat.controller;

import com.vstr.video_chat.model.Transmision;
import com.vstr.video_chat.repository.TransmisionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.util.Optional;

@Controller
public class TransmisionController {

    @Autowired
    private TransmisionRepository transmisionRepository;

    // Endpoint para obtener la transmisión activa
    @GetMapping("/transmision-activa")
    @ResponseBody
    public ResponseEntity<Transmision> getTransmisionActiva() {
        Optional<Transmision> transmisionOpt = transmisionRepository.findByEnVivoTrue().stream().findFirst();
        if (transmisionOpt.isPresent()) {
            return ResponseEntity.ok(transmisionOpt.get());
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    // Endpoint para la página de transmisiones
    @GetMapping("/transmisiones")
    public String transmisiones(Model model, Principal principal) {
        // Obtiene la transmisión activa
        Optional<Transmision> transmisionOpt = transmisionRepository.findByEnVivoTrue().stream().findFirst();
        transmisionOpt.ifPresent(transmision -> model.addAttribute("transmisionActiva", transmision));
        return "transmisiones";
    }

    @GetMapping("/transmision/{id}")
    public String verTransmision(@PathVariable Long id, Model model) {
        Optional<Transmision> transmisionOpt = transmisionRepository.findById(id);
        if (transmisionOpt.isPresent()) {
            model.addAttribute("transmision", transmisionOpt.get());
            return "transmision"; // Nombre de la plantilla Thymeleaf para ver la transmisión
        } else {
            model.addAttribute("error", "Transmisión no encontrada.");
            return "error"; // Asegúrate de tener una plantilla para manejar errores
        }
    }

    /*@GetMapping("/transmisiones-activas")
    public String mostrarTransmisionesActivas(Model model, Principal principal) {
        String username = principal.getName();
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + username));

        // Verificar el rol del usuario
        if (usuario.getRoles().stream().anyMatch(role -> role.getNameRol().equals("ROLE_STREAMER"))) {
            // Redirigir al dashboard del streamer
            return "redirect:/streamer/dashboard";
        } else if (usuario.getRoles().stream().anyMatch(role -> role.getNameRol().equals("ROLE_VIEWER"))) {
            // Redirigir al dashboard del viewer
            return "redirect:/viewer/dashboard";
        }

        // Si no tiene ningún rol específico, se puede mostrar la página de transmisiones activas
        List<Transmision> transmisionesActivas = transmisionRepository.findByEnVivoTrue();
        model.addAttribute("transmisionActiva", transmisionesActivas.isEmpty() ? null : transmisionesActivas.get(0));
        return "transmisiones"; // Retorna la vista de transmisiones activas
    }*/

}
