package com.vstr.video_chat.controller;

import com.vstr.video_chat.model.Video;
import com.vstr.video_chat.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.core.io.Resource;

import java.io.IOException;

import java.security.Principal;
import java.util.List;

@Controller
public class VideoController {

    @Autowired
    private VideoService videoService;

    @Autowired
    public VideoController(VideoService videoService) {
        this.videoService = videoService;
    }

    // Mostrar el formulario para cargar videos y listar videos existentes
    @GetMapping("/cargar-video")
    public String mostrarFormularioCargarVideo(Model model, Principal principal) {
        List<Video> videosPublicos = videoService.obtenerVideosPublicos();
        List<Video> videosPrivados = videoService.obtenerVideosPrivados(principal.getName());
        model.addAttribute("videosPublicos", videosPublicos);
        model.addAttribute("videosPrivados", videosPrivados);
        return "cargar-video"; // Nombre de tu plantilla Thymeleaf
    }

    // Manejar la subida de videos
    @PostMapping("/cargar-video")
    public String cargarVideo(@RequestParam("titulo") String titulo,
                              @RequestParam("video") MultipartFile videoFile,
                              @RequestParam(value = "publico", required = false, defaultValue = "false") boolean publico,
                              Model model,
                              Principal principal) {
        String username = principal.getName();
        try {
            videoService.subirVideo(titulo, videoFile, publico, username);
            model.addAttribute("mensaje", "Video cargado exitosamente.");
        } catch (IOException e) {
            throw new RuntimeException("Error al cargar el video: " + e.getMessage(), e);
        } catch (RuntimeException e) {
            throw e;
        }

        // Redirigir a la lista de videos cargados
        return "redirect:/cargar-video";
    }

    // Endpoint para servir los videos por ID
    @GetMapping("/video/{id}")
    @ResponseBody
    public ResponseEntity<Resource> servirVideo(@PathVariable Long id, Principal principal) {
        Video video = videoService.obtenerVideoPorId(id)
                .orElseThrow(() -> new RuntimeException("Video no encontrado con ID: " + id));

        // Verificar si el video es público o si el usuario es el propietario
        if (!video.isPublico() && !videoService.esPropietario(video, principal.getName())) {
            throw new RuntimeException("No tienes permiso para ver este video.");
        }

        byte[] contenido = video.getContenido();

        Resource resource = new ByteArrayResource(contenido);

        // Determinar el tipo de contenido basado en la extensión
        String contentType = determinarTipoContenido(video.getExtension());

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);
    }

    /**
     * Determinar el tipo de contenido basado en la extensión del archivo
     *
     * @param extension Extensión del archivo
     * @return Tipo de contenido MIME
     */
    private String determinarTipoContenido(String extension) {
        if (extension == null) {
            return "application/octet-stream"; // Tipo de contenido por defecto
        }
        switch (extension) {
            case "mp4":
                return "video/mp4";
            case "avi":
                return "video/x-msvideo";
            case "mov":
                return "video/quicktime";
            case "wmv":
                return "video/x-ms-wmv";
            case "mkv":
                return "video/x-matroska";
            default:
                return "application/octet-stream";
        }
    }

    // Eliminar un video
    @PostMapping("/video/eliminar/{id}")
    public String eliminarVideo(@PathVariable Long id, Principal principal) {
        Video video = videoService.obtenerVideoPorId(id)
                .orElseThrow(() -> new RuntimeException("Video no encontrado con ID: " + id));

        // Verificar si el usuario es el propietario
        if (!videoService.esPropietario(video, principal.getName())) {
            throw new RuntimeException("No tienes permiso para eliminar este video.");
        }

        videoService.eliminarVideo(video);

        return "redirect:/cargar-video";
    }

    // Cambiar la visibilidad de un video
    @PostMapping("/video/cambiar-visibilidad/{id}")
    public String cambiarVisibilidadVideo(@PathVariable Long id, Principal principal) {
        Video video = videoService.obtenerVideoPorId(id)
                .orElseThrow(() -> new RuntimeException("Video no encontrado con ID: " + id));

        // Verificar si el usuario es el propietario
        if (!videoService.esPropietario(video, principal.getName())) {
            throw new RuntimeException("No tienes permiso para cambiar la visibilidad de este video.");
        }

        videoService.cambiarVisibilidad(video);

        return "redirect:/cargar-video";
    }

    @GetMapping("/videos-publicos")
    public String obtenerVideosPublicos(Model model) {
        List<Video> videosPublicos = videoService.obtenerVideosPublicos(); // Método que devuelve solo videos públicos
        model.addAttribute("videosPublicos", videosPublicos);
        return "videos-publicos"; // Nombre de la plantilla Thymeleaf (videosPublicos.html)
    }

    // Método para cambiar la visibilidad del video
    @PostMapping("/video/{id}/toggle-public")
    public String togglePublic(@PathVariable Long id, Principal principal, Model model) {
        String username = principal.getName();
        Video video = videoService.obtenerVideoPorId(id)
                .orElseThrow(() -> new RuntimeException("Video no encontrado con ID: " + id));

        if (!videoService.esPropietario(video, username)) {
            model.addAttribute("error", "No tienes permiso para cambiar la visibilidad de este video.");
            return "redirect:/cargar-video";
        }

        videoService.cambiarVisibilidad(video);
        model.addAttribute("mensaje", "Visibilidad cambiada exitosamente.");
        return "redirect:/cargar-video";
    }

    @PostMapping("/video/{id}/delete")
    public String deleteVideo(@PathVariable Long id, Principal principal, Model model) {
        String username = principal.getName();
        Video video = videoService.obtenerVideoPorId(id)
                .orElseThrow(() -> new RuntimeException("Video no encontrado con ID: " + id));

        if (!videoService.esPropietario(video, username)) {
            model.addAttribute("error", "No tienes permiso para eliminar este video.");
            return "redirect:/cargar-video";
        }

        videoService.eliminarVideo(video);
        model.addAttribute("mensaje", "Video eliminado exitosamente.");
        return "redirect:/cargar-video";
    }
}
