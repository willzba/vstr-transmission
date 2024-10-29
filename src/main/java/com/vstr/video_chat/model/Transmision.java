package com.vstr.video_chat.model;

import jakarta.persistence.*;

@Entity
public class Transmision {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String streamerUsername;

    private String titulo;

    @Column(name = "en_vivo")
    private boolean enVivo;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    public Transmision() {
    }

    public Transmision(Long id, String titulo, boolean enVivo, Usuario usuario) {
        this.id = id;
        this.titulo = titulo;
        this.enVivo = enVivo;
        this.usuario = usuario;
    }

    public String getStreamerUsername() {
        return streamerUsername;
    }

    public void setStreamerUsername(String streamerUsername) {
        this.streamerUsername = streamerUsername;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public boolean isEnVivo() {
        return enVivo;
    }

    public void setEnVivo(boolean enVivo) {
        this.enVivo = enVivo;
    }
}
