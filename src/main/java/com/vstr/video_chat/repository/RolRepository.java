package com.vstr.video_chat.repository;

import com.vstr.video_chat.model.Rol;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RolRepository extends JpaRepository<Rol,Long> {
    Rol findByNameRol(String nameRol);
}
