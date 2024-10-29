package com.vstr.video_chat.controller;

import com.vstr.video_chat.model.Usuario;
import com.vstr.video_chat.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    @Autowired
    private UsuarioService usuarioService;

    // Página de registro
    @GetMapping("/register")
    public String mostrarFormularioRegistro(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "register";
    }

    // Página de registro
    @GetMapping("/login")
    public String mostrarLogin(Model model) {
        return "login";
    }

    // Manejar el registro de usuarios
    @PostMapping("/register")
    public String registrarUsuario(@ModelAttribute Usuario usuario, @RequestParam String rol, Model model) {
        try {
            Usuario nuevoUsuario = usuarioService.registrarUsuario(usuario.getUsername(), usuario.getPassword(), rol);
            model.addAttribute("mensaje", "Usuario registrado exitosamente.");
            return "login";
        } catch (Exception e) {
            model.addAttribute("error", "Error al registrar el usuario: " + e.getMessage());
            return "register";
        }
    }
}
