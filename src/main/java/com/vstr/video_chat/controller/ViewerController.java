package com.vstr.video_chat.controller;

import com.vstr.video_chat.model.Video;
import com.vstr.video_chat.service.VideoService;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class ViewerController {
    private final VideoService videoService;

    // Inyección de dependencias por constructor
    @Autowired
    public ViewerController(VideoService videoService) {
        this.videoService = videoService;
    }

    @GetMapping("/viewer/dashboard")
    public String mostrarDashboardViewer(Model model) {
        // Puedes agregar contenido adicional al dashboard si lo deseas
        return "dashboard-viewer"; // dashboard-viewer.html
    }

    @GetMapping("/viewer/videos-publicos")
    public String mostrarVideosPublicos(Model model) {
        List<Video> videosPublicos = videoService.obtenerVideosPublicos();
        model.addAttribute("videosPublicos", videosPublicos);
        return "videos-publicos"; // Nombre de la plantilla Thymeleaf para videos públicos
    }
}
