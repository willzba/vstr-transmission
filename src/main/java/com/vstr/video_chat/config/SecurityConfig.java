package com.vstr.video_chat.config;

import com.vstr.video_chat.service.CustomAuthenticationSuccessHandler;
import com.vstr.video_chat.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
public class SecurityConfig {
    /*@Bean

    //configuracion de usuarios en memoria
    public InMemoryUserDetailsManager userDetailsService() {
        UserDetails streamer = User.withDefaultPasswordEncoder()
                .username("streamer")
                .password("123")
                .roles("STREAMER")
                .build();

        UserDetails viewer = User.withDefaultPasswordEncoder()
                .username("viewer")
                .password("123")
                .roles("VIEWER")
                .build();

        return new InMemoryUserDetailsManager(streamer, viewer);
    }*/

    private final CustomUserDetailsService customUserDetailsService;
    private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;


    @Value("${video.storage.path}")
    private String storagePath;

    @Autowired
    public SecurityConfig(CustomUserDetailsService customUserDetailsService, CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler) {
        this.customUserDetailsService = customUserDetailsService;
        this.customAuthenticationSuccessHandler = customAuthenticationSuccessHandler;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    // Configurar el AuthenticationManager para usar el CustomUserDetailsService
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        authBuilder
                .userDetailsService(customUserDetailsService)
                .passwordEncoder(passwordEncoder());
        return authBuilder.build();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                /*.csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()) // Usa cookies para CSRF
                )*/
                .authorizeHttpRequests(authorize -> authorize
                        // Permitir acceso libre a recursos estáticos
                        .requestMatchers("/register", "/login", "/css/**", "/js/**","/videos/**", "/images/**").permitAll()
                        // Rutas específicas para roles
                        .requestMatchers("/iniciar-transmision/**", "/actualizar-transmision-a-vivo").hasRole("STREAMER")
                        .requestMatchers("/viewer/dashboard/**", "/viewer/videos-publicos/**").hasRole("VIEWER")
                        .requestMatchers("/ver-transmision/**","/video/**").hasAnyRole("STREAMER", "VIEWER")
                        .requestMatchers("/cargar-video/**").hasRole("STREAMER")
                        .requestMatchers("/actualizar-transmision-a-vivo").hasRole("STREAMER")
                        .requestMatchers("/videos-publicos/**").permitAll()
                        // Cualquier otra solicitud requiere autenticación
                        .anyRequest().authenticated()

                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .successHandler(customAuthenticationSuccessHandler) // Utilizar el handler personalizado
                        //.defaultSuccessUrl("/dashboard", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                );


        return http.build();
    }

}
