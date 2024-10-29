package com.vstr.video_chat.model;

import jakarta.persistence.*;

import java.util.Set;

@Entity
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String nameRol;

    @ManyToMany(mappedBy = "roles")
    private Set<Usuario> usuarios;

    public Rol() {
    }

    public Rol(Long id, String nameRol, Set<Usuario> usuarios) {
        this.id = id;
        this.nameRol = nameRol;
        this.usuarios = usuarios;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNameRol() {
        return nameRol;
    }

    public void setNameRol(String nameRol) {
        this.nameRol = nameRol;
    }

    public Set<Usuario> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(Set<Usuario> usuarios) {
        this.usuarios = usuarios;
    }
}
