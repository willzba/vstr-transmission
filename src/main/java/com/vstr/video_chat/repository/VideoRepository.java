package com.vstr.video_chat.repository;

import com.vstr.video_chat.model.Usuario;
import com.vstr.video_chat.model.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface VideoRepository extends JpaRepository<Video, Long> {
    List<Video> findByPublicoTrue();
    Optional<Video> findByUrl(String url);
    List<Video> findByPublicoFalseAndUsuario(Usuario usuario);

    @Query("SELECT v FROM Video v WHERE v.usuario.username = :username AND v.publico = false")
    List<Video> findPrivadosByUsuarioUsername(String username);
}
