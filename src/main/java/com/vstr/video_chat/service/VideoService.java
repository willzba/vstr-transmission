package com.vstr.video_chat.service;

import com.vstr.video_chat.exception.FileTypeNotAllowedException;
import com.vstr.video_chat.exception.UsuarioNotFoundException;
import com.vstr.video_chat.model.Usuario;
import com.vstr.video_chat.model.Video;
import com.vstr.video_chat.repository.UsuarioRepository;
import com.vstr.video_chat.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class VideoService {
    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Método para subir un video
     *
     * @param titulo    Título del video
     * @param file      Archivo de video
     * @param publico   Indicador de si el video es público
     * @param username  Nombre de usuario que sube el video
     * @return          Objeto Video guardado
     * @throws IOException Si ocurre un error al leer el archivo
     */
    public Video subirVideo(String titulo, MultipartFile file, boolean publico, String username) throws IOException {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsuarioNotFoundException("Usuario no encontrado: " + username));

        // Limpiar el nombre del archivo
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        // Validar el nombre del archivo
        if(fileName.contains("..")) {
            throw new RuntimeException("Nombre de archivo inválido: " + fileName);
        }

        // Extraer la extensión del archivo
        String extension = "";
        int index = fileName.lastIndexOf('.');
        if (index > 0) {
            extension = fileName.substring(index + 1).toLowerCase();
        }

        // Validar el tipo de archivo
        if (extension.isEmpty()) {
            throw new RuntimeException("El archivo no tiene una extensión válida.");
        }

        if (!isAllowedExtension(extension)) {
            throw new FileTypeNotAllowedException("Tipo de archivo no permitido: ." + extension);
        }

        // Convertir el archivo a bytes
        byte[] contenido = file.getBytes();

        // Crear el objeto Video
        Video video = new Video();
        video.setTitulo(titulo);
        video.setContenido(contenido); // Establecer contenido
        video.setPublico(publico);
        video.setUsuario(usuario);
        video.setExtension(extension); // Establecer extensión

        return videoRepository.save(video);
    }


    /**
     * Obtener una lista de videos públicos
     *
     * @return Lista de videos públicos
     */
    public List<Video> obtenerVideosPublicos() {
        return videoRepository.findByPublicoTrue();
    }

    /**
     * Obtener una lista de videos privados del usuario
     *
     * @param username Nombre de usuario
     * @return Lista de videos privados del usuario
     */
    public List<Video> obtenerVideosPrivados(String username) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsuarioNotFoundException("Usuario no encontrado: " + username));
        return videoRepository.findByPublicoFalseAndUsuario(usuario);
    }

    /**
     * Obtener un video por su ID
     *
     * @param id ID del video
     * @return Optional con el video si existe
     */
    public Optional<Video> obtenerVideoPorId(Long id) {
        return videoRepository.findById(id);
    }

    /**
     * Verificar si el usuario es el propietario del video
     *
     * @param video     Objeto Video
     * @param username  Nombre de usuario
     * @return          true si es el propietario, false en caso contrario
     */
    public boolean esPropietario(Video video, String username) {
        return video.getUsuario().getUsername().equals(username);
    }

    /**
     * Cambiar el estado de visibilidad de un video
     *
     * @param video Video a actualizar
     */
    public void cambiarVisibilidad(Video video) {
        video.setPublico(!video.isPublico());
        videoRepository.save(video);
    }

    /**
     * Eliminar un video
     *
     * @param video Video a eliminar
     */
    public void eliminarVideo(Video video) {
        videoRepository.delete(video);
    }

    public void cargarVideo(Video video) {
        videoRepository.save(video);
    }

    /**
     * Método para verificar si la extensión del archivo está permitida
     *
     * @param extension Extensión del archivo
     * @return true si está permitida, false en caso contrario
     */
    private boolean isAllowedExtension(String extension) {
        List<String> allowedExtensions = Arrays.asList("mp4", "avi", "mov", "wmv", "mkv"); // Agrega más según sea necesario
        return allowedExtensions.contains(extension);
    }
}
