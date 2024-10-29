package com.vstr.video_chat.model;

import jakarta.persistence.*;

import java.util.Set;

@Entity
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "usuario_roles",
            joinColumns = @JoinColumn(name = "usuario_id"),
            inverseJoinColumns = @JoinColumn(name = "rol_id")
    )
    private Set<Rol> roles;

    @OneToMany(mappedBy = "usuario")
    private Set<Transmision> transmisiones;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Video> videos;

    public Usuario() {
    }

    public Usuario(Long id, String username, String password, Set<Rol> roles, Set<Transmision> transmisiones, Set<Video> videos) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.roles = roles;
        this.transmisiones = transmisiones;
        this.videos = videos;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<Rol> getRoles() {
        return roles;
    }

    public void setRoles(Set<Rol> roles) {
        this.roles = roles;
    }

    public Set<Transmision> getTransmisiones() {
        return transmisiones;
    }

    public void setTransmisiones(Set<Transmision> transmisiones) {
        this.transmisiones = transmisiones;
    }

    public Set<Video> getVideos() {
        return videos;
    }

    public void setVideos(Set<Video> videos) {
        this.videos = videos;
    }

}
