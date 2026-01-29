package com.daw.persistence.repositories;

import java.util.List;

import org.springframework.data.repository.ListCrudRepository;

import com.daw.persistence.entities.Estado;
import com.daw.persistence.entities.Tarea;

public interface TareaRepository extends ListCrudRepository<Tarea, Integer> {
	
	List<Tarea> findByEstado(Estado estado);	
	List<Tarea> findByUsuarioUsername(String username);


}
