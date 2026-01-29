package com.daw.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.daw.persistence.entities.Usuario;
import com.daw.persistence.repositories.UsuarioRepository;

@Service
public class UsuarioService implements UserDetailsService {
	
	@Autowired
	private UsuarioRepository usuarioRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		Usuario usuario = this.usuarioRepository.findByUsername(username).orElseThrow(
				() -> new UsernameNotFoundException("El usuario " + username + " no existe. "));
		
		return User.builder()
				.username(usuario.getUsername())
				.password(usuario.getPassword())
				.roles(usuario.getRol())
				.build();
	}

}
