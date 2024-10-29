package com.vstr.video_chat.repository;

import com.vstr.video_chat.model.Transmision;
import com.vstr.video_chat.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TransmisionRepository extends JpaRepository<Transmision, Long> {

    List<Transmision> findByEnVivoTrue();
    List<Transmision> findByEnVivoFalse();
    Optional<Transmision> findByUsuarioAndEnVivoTrue(Usuario usuario);
    Transmision findByStreamerUsername(String username);

}
