package com.vstr.video_chat.service;

import com.vstr.video_chat.model.Rol;
import com.vstr.video_chat.model.Usuario;
import com.vstr.video_chat.repository.RolRepository;
import com.vstr.video_chat.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Usuario registrarUsuario(String username, String password, String rolNombre) {
        if (usuarioRepository.findByUsername(username) != null) {
            throw new RuntimeException("Usuario ya existe con el nombre de usuario: " + username);
        }

        Usuario usuario = new Usuario();
        usuario.setUsername(username);
        usuario.setPassword(passwordEncoder.encode(password));

        Rol rol = rolRepository.findByNameRol(rolNombre);
        if (rol == null) {
            rol = new Rol();
            rol.setNameRol(rolNombre);
            rolRepository.save(rol);
        }

        HashSet<Rol> roles = new HashSet<>();
        roles.add(rol);
        usuario.setRoles(roles);

        return usuarioRepository.save(usuario);
    }
}
