package com.vstr.video_chat.model;

import jakarta.persistence.*;

@Entity
public class Video {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(columnDefinition = "longblob")
    private byte[] contenido;

    private String titulo;

    private String url; // Ruta o URL del video

    private boolean publico;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    private String extension;

    public Video() {
    }

    public Video(Long id, byte[] contenido, String titulo, String url, boolean publico, Usuario usuario, String extension) {
        this.id = id;
        this.contenido = contenido;
        this.titulo = titulo;
        this.url = url;
        this.publico = publico;
        this.usuario = usuario;
        this.extension = extension;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public byte[] getContenido() {
        return contenido;
    }

    public void setContenido(byte[] contenido) {
        this.contenido = contenido;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isPublico() {
        return publico;
    }

    public void setPublico(boolean publico) {
        this.publico = publico;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}
