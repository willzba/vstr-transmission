package com.vstr.video_chat.controller;

import com.vstr.video_chat.Dto.TransmisionDto;
import com.vstr.video_chat.model.Transmision;
import com.vstr.video_chat.model.Usuario;
import com.vstr.video_chat.model.WebRTCMessage;
import com.vstr.video_chat.repository.TransmisionRepository;
import com.vstr.video_chat.repository.UsuarioRepository;
import com.vstr.video_chat.service.TransmisionService;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.util.Map;
import java.util.Optional;


@Controller
public class StreamController {

    @Autowired
    private TransmisionService transmisionService;

    @Autowired
    private TransmisionRepository transmisionRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    private static final Logger logger = LoggerFactory.getLogger(StreamController.class);


    @GetMapping("/streamer/dashboard")
    public String mostrarDashboardStreamer(Model model) {
        // Puedes agregar contenido adicional al dashboard si lo deseas
        return "dashboard-streamer";  // El nombre del archivo HTML
    }

    @GetMapping("/cargar-videos")
    public String mostrarCargarVideos(Model model) {
        // Aquí puedes añadir atributos al modelo si los necesitas
        // model.addAttribute("nombre", "valor");

        return "cargar-videos"; // Devuelve el nombre de la plantilla sin la extensión .html
    }

    // Endpoint para iniciar una transmisión
    @GetMapping("/iniciar-transmision")
    @PreAuthorize("hasRole('STREAMER')")
    public String iniciarTransmision(Model model, Principal principal) {
        String username = principal.getName();
        logger.info("Usuario {} está intentando iniciar una transmisión.", username);

        // Obtener el usuario que está intentando iniciar la transmisión
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + username));

        // Verificar si el usuario ya tiene una transmisión activa
        Optional<Transmision> transmisionActiva = transmisionRepository.findByUsuarioAndEnVivoTrue(usuario);
        if (transmisionActiva.isPresent()) {
            logger.warn("Usuario {} ya tiene una transmisión activa. No puede iniciar otra.", username);
            model.addAttribute("error", "Ya tienes una transmisión activa.");
            return "error";
        }

        // Crear una nueva transmisión para el usuario
        Transmision transmision = new Transmision();
        transmision.setTitulo("Transmisión en Vivo de " + usuario.getUsername());
        transmision.setEnVivo(false); // La transmisión comienza inactiva
        transmision.setUsuario(usuario);

        try {
            // Guardar la nueva transmisión en la base de datos
            transmisionRepository.save(transmision);
            logger.info("Transmisión iniciada: ID={}, Streamer={}", transmision.getId(), usuario.getUsername());

            // Añadir la transmisión al modelo para ser usada en la vista
            model.addAttribute("transmision", transmision);
            model.addAttribute("transmisionId", transmision.getId()); // Enviar el ID al frontend si es necesario

            return "iniciar-transmision"; // Retorna la página de inicio de transmisión
        } catch (Exception e) {
            logger.error("Error al iniciar la transmisión para el usuario {}: {}", username, e.getMessage());
            model.addAttribute("error", "Error al iniciar la transmisión. Por favor, inténtalo de nuevo.");
            return "error"; // Mostrar página de error si ocurre un problema
        }
    }

    @PostMapping("/actualizar-transmision-a-vivo")
    @PreAuthorize("hasRole('STREAMER')")
    public ResponseEntity<String> actualizarTransmisionAVivo(@RequestBody Map<String, Long> payload) {
        Long transmisionId = payload.get("transmisionId");

        // Buscar la transmisión por ID
        Transmision transmision = transmisionRepository.findById(transmisionId)
                .orElseThrow(() -> new RuntimeException("Transmisión no encontrada con ID: " + transmisionId));

        // Actualizar el estado de la transmisión a "en vivo"
        transmision.setEnVivo(true);
        transmisionRepository.save(transmision);

        logger.info("Transmisión {} ha sido actualizada a 'en vivo'.", transmisionId);

        return ResponseEntity.ok("Transmisión iniciada");
    }

    @PostMapping("/actualizar-transmision-a-no-vivo")
    @PreAuthorize("hasRole('STREAMER')")
    @ResponseBody
    public ResponseEntity<?> actualizarTransmisionANoVivo(@RequestBody Map<String, Long> payload) {
        Long transmisionId = payload.get("transmisionId");

        // Busca la transmisión por ID
        Optional<Transmision> optionalTransmision = transmisionRepository.findById(transmisionId);
        if (optionalTransmision.isPresent()) {
            Transmision transmision = optionalTransmision.get();
            transmision.setEnVivo(false); // Cambia el estado a false
            transmisionRepository.save(transmision); // Guarda los cambios en la base de datos

            // Notifica al streamer que la transmisión ha sido terminada
            String streamerUsername = transmision.getUsuario().getUsername();
            String notificationMessage = "La transmisión ha terminado debido a un evento imprevisto.";

            // Envía el mensaje a través de WebSocket
            messagingTemplate.convertAndSendToUser(streamerUsername, "/queue/notifications", notificationMessage);

            return ResponseEntity.ok("Transmisión actualizada a no viva.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Transmisión no encontrada.");
        }
    }


    // Endpoint para detener una transmisión
    @GetMapping("/detener-transmision")
    public String detenerTransmision(Model model, Principal principal) {
        logger.info("Usuario {} está intentando detener una transmisión.", principal.getName());

        Optional<Transmision> transmisionActiva = transmisionRepository.findByEnVivoTrue().stream().findFirst();
        if (transmisionActiva.isPresent()) {
            Transmision transmision = transmisionActiva.get();
            logger.info("Transmisión activa encontrada: ID={}, Streamer={}", transmision.getId(), transmision.getUsuario().getUsername());

            // Verificar si el usuario actual es el streamer de la transmisión
            if (transmision.getUsuario().getUsername().equals(principal.getName())) {
                transmision.setEnVivo(false);
                transmisionRepository.save(transmision);
                logger.info("Transmisión ID={} detenida por el streamer {}.", transmision.getId(), principal.getName());

                // Notificar a los viewers que la transmisión ha sido detenida
                WebRTCMessage stopMessage = new WebRTCMessage();
                stopMessage.setFrom("streamer");
                stopMessage.setTo("all");
                stopMessage.setType("stop");
                stopMessage.setPayload(null);
                messagingTemplate.convertAndSend("/topic/webrtc", stopMessage);
                logger.info("Mensaje 'stop' enviado a todos los viewers.");

                // Informar al usuario sobre la detención
                model.addAttribute("mensaje", "Transmisión detenida correctamente. Ya puedes iniciar una nueva transmisión.");

                // Retornar la vista donde se puede iniciar una nueva transmisión
                return "iniciar-transmision"; // Cambia a la vista de nueva transmisión
            } else {
                logger.warn("Usuario {} no tiene permiso para detener la transmisión ID={}.", principal.getName(), transmision.getId());
                model.addAttribute("error", "No tienes permiso para detener esta transmisión.");
                return "error"; // Esto puede ser una vista de error más amigable
            }
        }

        logger.warn("No hay transmisiones activas para detener.");
        model.addAttribute("mensaje", "Transmision detenida, Dale en el boton \uD83D\uDC47. (GRACIAS)."); // Mensaje más amigable
        return "error"; // Cambia a la vista de nueva transmisión
    }

}
