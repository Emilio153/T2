package com.daw.services;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.daw.persistence.entities.Estado;
import com.daw.persistence.entities.Tarea;
import com.daw.persistence.repositories.TareaRepository;
import com.daw.services.exceptions.TareaException;
import com.daw.services.exceptions.TareaNotFoundException;
import com.daw.services.exceptions.TareaSecurityException;

@Service
public class TareaService {

	@Autowired
	private TareaRepository tareaRepository;

	// findAll
	public List<Tarea> findAll() {
		return this.tareaRepository.findAll();
	}

	// findById
	public Tarea findById(int idTarea) {
		if (!this.tareaRepository.existsById(idTarea)) {
			throw new TareaNotFoundException("La tarea con id " + idTarea + " no existe. ");
		}

		return this.tareaRepository.findById(idTarea).get();
	}

	// create
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

		tarea.setId(0);
		tarea.setEstado(Estado.PENDIENTE);
		tarea.setFechaCreacion(LocalDate.now());

		return this.tareaRepository.save(tarea);
	}

	// update
	public Tarea update(Tarea tarea, int idTarea) {
		if (tarea.getId() != idTarea) {
			throw new TareaException(
					String.format("El id del body (%d) y el id del path (%d) no coinciden", tarea.getId(), idTarea));
		}
		if (!this.tareaRepository.existsById(idTarea)) {
			throw new TareaNotFoundException("La tarea con id " + idTarea + " no existe. ");
		}
		if (tarea.getEstado() != null) {
			throw new TareaException("No se puede modificar el estado. ");
		}
		if (tarea.getFechaCreacion() != null) {
			throw new TareaException("No se puede modificar la fecha de creación. ");
		}

		// Recupero la tarea que está en BBDD y modifico solo los campos permitidos.
		// Si guardo directamente tarea, voy a poner a null la fecha de creación y el
		// estado.
		Tarea tareaBD = this.findById(idTarea);
		tareaBD.setDescripcion(tarea.getDescripcion());
		tareaBD.setTitulo(tarea.getTitulo());
		tareaBD.setFechaVencimiento(tarea.getFechaVencimiento());

		return this.tareaRepository.save(tareaBD);
	}

	// delete
	public void delete(int idTarea) {
		if (!this.tareaRepository.existsById(idTarea)) {
			throw new TareaNotFoundException("La tarea no existe");
		}
		this.tareaRepository.deleteById(idTarea);
	}

	public Tarea marcarEnProgreso(int idTarea) {
		Tarea tarea = this.findById(idTarea);

		if (!tarea.getEstado().equals(Estado.PENDIENTE)) {
			throw new TareaException("La tarea ya está completada o ya está en progreso");
		}

		tarea.setEstado(Estado.EN_PROGRESO);
		return this.tareaRepository.save(tarea);
	}
	
//	Obtener las tareas pendientes.
	public List<Tarea> pendientes() {
		return this.tareaRepository.findByEstado(Estado.PENDIENTE);
	}
	
//	Obtener las tareas en progreso.
	public List<Tarea> enProgreso() {
		return this.tareaRepository.findByEstado(Estado.EN_PROGRESO);
	}
//	Obtener las tareas completadas.
	public List<Tarea> completadas() {
		return this.tareaRepository.findByEstado(Estado.COMPLETADA);
	}

	//Métodos securizados
	//Mis tareas
	public List<Tarea> findByUser() {
		//Con este método obtengo el username que está autenticado en este momento
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		
		return this.tareaRepository.findByUsuarioUsername(username);
	}
	
	public Tarea findByIdAndUser(int idTarea) {
		Tarea t = this.findById(idTarea);
		
		if(!t.getUsuario().getUsername().equals(SecurityContextHolder.getContext().getAuthentication().getName())) {
			throw new TareaSecurityException("La tarea no pertenece al usuario. ");
		}

		return t;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
}
