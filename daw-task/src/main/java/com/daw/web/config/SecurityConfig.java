package com.daw.web.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			.csrf(csrf -> csrf.disable()) // Desactivado según página 16 de los apuntes
			.cors(cors -> cors.configurationSource(corsConfigurationSource()))
			.authorizeHttpRequests(auth -> auth
				// 1. Permisos para listar y ver detalles (Ambos roles)
				.requestMatchers(HttpMethod.GET, "/tareas", "/tareas/*", "/tareas/pendientes", "/tareas/en-progreso", "/tareas/completadas").hasAnyRole("ADMIN", "USER")
				
				// 2. Permiso para crear tareas (Ambos roles)
				.requestMatchers(HttpMethod.POST, "/tareas").hasAnyRole("ADMIN", "USER")
				
				// 3. Permisos para modificar el estado (iniciar y completar) (Ambos roles)
				.requestMatchers(HttpMethod.PUT, "/tareas/*/iniciar", "/tareas/*/completar").hasAnyRole("ADMIN", "USER")
				
				// 4. CONTROL TOTAL ADMIN: Solo el administrador puede Editar (PUT completo) y Borrar (DELETE)
				// Estas rutas corresponden a los métodos update y delete del controlador
				.requestMatchers(HttpMethod.PUT, "/tareas/*").hasRole("ADMIN")
				.requestMatchers(HttpMethod.DELETE, "/tareas/*").hasRole("ADMIN")
				
				.anyRequest().authenticated()
			)
			.httpBasic(Customizer.withDefaults()); // Autenticación básica según página 13
			
		return http.build();
	}
	
	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(); // Según página 26 de los apuntes
	}
	
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        List<String> allowedOrigins = Arrays.asList("http://localhost:4200");
        configuration.setAllowedOrigins(allowedOrigins);
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept"));
        configuration.setExposedHeaders(List.of("Authorization"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}