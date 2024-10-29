package com.vstr.video_chat.dataInicializer;

import com.vstr.video_chat.model.Rol;
import com.vstr.video_chat.repository.RolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;

public class DataInitializer implements CommandLineRunner {

    @Autowired
    private RolRepository rolRepository;

    @Override
    public void run(String... args) throws Exception {
        // Verificar y crear roles si no existen
        if (rolRepository.findByNameRol("ROLE_STREAMER") == null) {
            Rol streamerRol = new Rol();
            streamerRol.setNameRol("ROLE_STREAMER");
            rolRepository.save(streamerRol);
        }

        if (rolRepository.findByNameRol("ROLE_VIEWER") == null) {
            Rol viewerRol = new Rol();
            viewerRol.setNameRol("ROLE_VIEWER");
            rolRepository.save(viewerRol);
        }
    }
}
