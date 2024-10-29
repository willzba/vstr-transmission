package com.vstr.video_chat.service;

import com.vstr.video_chat.model.Transmision;
import com.vstr.video_chat.repository.TransmisionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransmisionService {

    @Autowired
    private TransmisionRepository transmisionRepository;

    public Transmision findTransmisionByStreamer(String username) {
        return transmisionRepository.findByStreamerUsername(username); // Asegúrate de implementar este método en tu repositorio
    }

    public Transmision save(Transmision transmision) {
        return transmisionRepository.save(transmision);
    }

}
