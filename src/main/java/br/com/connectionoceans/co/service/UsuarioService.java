package br.com.connectionoceans.co.service;

import java.nio.charset.Charset;
import java.time.Period;
import java.util.List;
import java.util.Optional;
import java.time.LocalDate;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import br.com.connectionoceans.co.model.Usuario;
import br.com.connectionoceans.co.model.UsuarioLogin;
import br.com.connectionoceans.co.repository.UsuarioRepository;

@Service
public class UsuarioService {
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
	
	public String encoder(String senha) {
		return encoder.encode(senha);
	}
	
	public List<Usuario> listarUsuarios(){
		return usuarioRepository.findAll();
	}
	
	public Optional<Usuario> buscarUsuarioId(long id) {
		return usuarioRepository.findById(id);
	}
	
	public Optional<Usuario> cadastrarUsuario(Usuario usuario) {
		if(usuarioRepository.findByUsuario(usuario.getUsuario()).isPresent())
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O usuário ja existe", null);
		
		int idade = Period.between(usuario.getDataNascimento(), LocalDate.now()).getYears();
		
		if(idade < 18)
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O usuário ja existe", null);
		
		usuario.setSenha(encoder(usuario.getSenha()));
		
		
		return Optional.of(usuarioRepository.save(usuario));
	}
	
	public Optional<Usuario> atualizarUsuario(Usuario usuario) {
		if(usuarioRepository.findByUsuario(usuario.getUsuario()).isPresent()) {
			int idade = Period.between(usuario.getDataNascimento(), LocalDate.now()).getYears();
			
			if(idade < 18)
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O usuário ja existe", null);
			
			usuario.setSenha(encoder(usuario.getSenha()));
			
			
			return Optional.of(usuarioRepository.save(usuario));
		
		}
		else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado", null);
		}
	}
	
	public Optional<UsuarioLogin> loginUsuario(Optional<UsuarioLogin> userLogin){
		Optional<Usuario> usuario = usuarioRepository.findByUsuario(userLogin.get().getUsuario());
		
		if(usuario.isPresent()) {
			if(encoder.matches(userLogin.get().getSenha(), usuario.get().getSenha())) {
				String auth = userLogin.get().getUsuario() + ":" + userLogin.get().getSenha();
				byte[] encodeAuth = Base64.encodeBase64(auth.getBytes(Charset.forName("US-ASCII")));
				String authHeader = "Basic " + new String(encodeAuth);
				
				
				userLogin.get().setId(usuario.get().getId());
				userLogin.get().setUsuario(usuario.get().getUsuario());
				userLogin.get().setNome(usuario.get().getNome());
				userLogin.get().setSenha(usuario.get().getSenha());
				userLogin.get().setFoto(usuario.get().getFoto());
				userLogin.get().setTipo(usuario.get().getTipo());
				userLogin.get().setToken(authHeader);
				
				return userLogin;
			}
		}
		
		throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Login Inválido", null);
	}

}
