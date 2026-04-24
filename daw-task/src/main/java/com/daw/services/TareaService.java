package com.daw.services;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.daw.persistence.entities.Estado;
import com.daw.persistence.entities.Tarea;
import com.daw.persistence.entities.Usuario;
import com.daw.persistence.repositories.TareaRepository;
import com.daw.persistence.repositories.UsuarioRepository;
import com.daw.services.exceptions.TareaException;
import com.daw.services.exceptions.TareaNotFoundException;
import com.daw.services.exceptions.TareaSecurityException;

@Service
public class TareaService {

	@Autowired
	private TareaRepository tareaRepository;
	
	@Autowired
	private UsuarioRepository usuarioRepository;

	// --- MÉTODOS DE APOYO PARA SEGURIDAD ---

	/**
	 * Comprueba si el usuario autenticado tiene el rol ADMIN 
	 */
	private boolean isAdmin() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		return auth.getAuthorities().stream()
				.anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
	}

	/**
	 * Obtiene el username del usuario autenticado [cite: 416]
	 */
	private String getCurrentUsername() {
		return SecurityContextHolder.getContext().getAuthentication().getName();
	}

	// --- MÉTODOS DE BÚSQUEDA ---

	// findById (Uso interno o para ADMIN sin restricciones de propiedad)
	public Tarea findById(int idTarea) {
		return this.tareaRepository.findById(idTarea)
				.orElseThrow(() -> new TareaNotFoundException("La tarea con id " + idTarea + " no existe. "));
	}

	// Listar tareas securizado [cite: 413]
	public List<Tarea> findByUser() {
		// Si es ADMIN, control total: ve todas las tareas del sistema 
		if (isAdmin()) {
			return this.tareaRepository.findAll();
		}
		// Si es USER, solo las suyas [cite: 384, 416]
		return this.tareaRepository.findByUsuarioUsername(getCurrentUsername());
	}

	// Buscar por ID securizado [cite: 417]
	public Tarea findByIdAndUser(int idTarea) {
		Tarea t = this.findById(idTarea);
		
		// Si no es ADMIN y la tarea no es suya, lanzamos excepción de seguridad [cite: 424, 430]
		if (!isAdmin() && !t.getUsuario().getUsername().equals(getCurrentUsername())) {
			throw new TareaSecurityException("La tarea no pertenece al usuario. Acceso denegado.");
		}

		return t;
	}

	// --- MÉTODOS DE GESTIÓN ---

	public Tarea create(Tarea tarea) {
		if (tarea.getFechaVencimiento().isBefore(LocalDate.now())) {
			throw new TareaException("La fecha de vencimiento debe ser posterior. ");
		}
		if (tarea.getEstado() != null) {
			throw new TareaException("No se puede modificar el estado. ");
		}
		if (tarea.getFechaCreacion() != null) {
			throw new TareaException("No se puede modificar la fecha de creación. ");
		}
		String username = getCurrentUsername(); // Usamos el método que creamos antes
	    Usuario usuarioLogueado = usuarioRepository.findByUsername(username)
	            .orElseThrow(() -> new TareaException("Usuario no encontrado en el sistema"));

		tarea.setId(0);
		tarea.setEstado(Estado.PENDIENTE);
		tarea.setFechaCreacion(LocalDate.now());
		
		tarea.setUsuario(usuarioLogueado);
		
		return this.tareaRepository.save(tarea);
	}

	public Tarea update(Tarea tarea, int idTarea) {
		if (tarea.getId() != idTarea) {
			throw new TareaException(
					String.format("El id del body (%d) y el id del path (%d) no coinciden", tarea.getId(), idTarea));
		}
		
		// Utilizamos findByIdAndUser para que el ADMIN pueda editar cualquiera [cite: 394]
		// y el USER sea bloqueado si intenta editar la de otro [cite: 410]
		Tarea tareaBD = this.findByIdAndUser(idTarea);
		
		if (tarea.getEstado() != null) {
			throw new TareaException("No se puede modificar el estado por este método. ");
		}
		if (tarea.getFechaCreacion() != null) {
			throw new TareaException("No se puede modificar la fecha de creación. ");
		}

		tareaBD.setDescripcion(tarea.getDescripcion());
		tareaBD.setTitulo(tarea.getTitulo());
		tareaBD.setFechaVencimiento(tarea.getFechaVencimiento());

		return this.tareaRepository.save(tareaBD);
	}

	public void delete(int idTarea) {
		// Al usar findByIdAndUser, permitimos que el ADMIN borre cualquiera [cite: 395]
		Tarea t = this.findByIdAndUser(idTarea);
		this.tareaRepository.delete(t);
	}

	public Tarea marcarEnProgreso(int idTarea) {
		// Validamos propiedad (o si es admin) antes de cambiar estado [cite: 387, 394]
		Tarea tarea = this.findByIdAndUser(idTarea);

		if (!tarea.getEstado().equals(Estado.PENDIENTE)) {
			throw new TareaException("La tarea ya está completada o ya está en progreso");
		}

		tarea.setEstado(Estado.EN_PROGRESO);
		return this.tareaRepository.save(tarea);
	}

    public Tarea marcarCompletada(int idTarea) {
        Tarea tarea = this.findByIdAndUser(idTarea);
        
        if (tarea.getEstado().equals(Estado.COMPLETADA)) {
            throw new TareaException("La tarea ya está completada");
        }
        
        tarea.setEstado(Estado.COMPLETADA);
        return this.tareaRepository.save(tarea);
    }
	
	// Filtros por estado (También deberían filtrarse por usuario si no es admin)
	public List<Tarea> pendientes() {
		return isAdmin() ? this.tareaRepository.findByEstado(Estado.PENDIENTE) 
				         : this.tareaRepository.findByEstadoAndUsuarioUsername(Estado.PENDIENTE, getCurrentUsername());
	}
	
	public List<Tarea> enProgreso() {
		return isAdmin() ? this.tareaRepository.findByEstado(Estado.EN_PROGRESO) 
				         : this.tareaRepository.findByEstadoAndUsuarioUsername(Estado.EN_PROGRESO, getCurrentUsername());
	}

	public List<Tarea> completadas() {
		return isAdmin() ? this.tareaRepository.findByEstado(Estado.COMPLETADA) 
				         : this.tareaRepository.findByEstadoAndUsuarioUsername(Estado.COMPLETADA, getCurrentUsername());
	}
}