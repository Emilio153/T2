package com.daw.persistence.entities;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tarea")
@Getter
@Setter
@NoArgsConstructor
public class Tarea {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	private String titulo;
	private String descripcion;

	@Column(name = "fecha_creacion")
	private LocalDate fechaCreacion;

	@Column(name = "fecha_vencimiento")
	private LocalDate fechaVencimiento;
	
	@Enumerated(value = EnumType.STRING)
	private Estado estado;
	
	@Column(name = "id_usuario")
	private int idUsuario;
	
	@ManyToOne
	@JoinColumn(name = "id_usuario", referencedColumnName = "id", insertable = false, updatable = false)
	@JsonIgnore
	private Usuario usuario;
	
}
