package com.application.ogawadev.application.service;

import java.util.NoSuchElementException;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.application.ogawadev.domain.Cliente.Cliente;
import com.application.ogawadev.domain.Cliente.ClienteRepository;
import com.application.ogawadev.domain.restaurante.Restaurante;
import com.application.ogawadev.domain.restaurante.RestauranteRepository;



@Service
public class ClienteService {
	
	@Autowired
	private ClienteRepository clienteRepository;
	
	@Autowired RestauranteRepository restauranteRepository;
	
	@Transactional
	public void saveCliente( Cliente cliente ) throws ValidationException{
		
		if(!validadeEmail(cliente.getEmail(), cliente.getId())){
			throw new ValidationException("O e-mail esta duplicado");
		}
		
		if(cliente.getId() != null) {
			
			Cliente clienteDB = clienteRepository.findById(cliente.getId()).orElseThrow(NoSuchElementException::new);
			cliente.setSenha(clienteDB.getSenha());
			
			
		} else {
			
			cliente.encryptPassword();
			
		}
		
		clienteRepository.save(cliente);
		
	}
	
	private boolean validadeEmail(String email, Integer id) {
		
		Restaurante restaurante = restauranteRepository.findByEmail(email);
		
		if(restaurante != null) {
			return false;
		}
		
		Cliente cliente = clienteRepository.findByEmail(email);
		
		if(cliente != null) {
			
			if( id == null ) {
				return false;
			}
			
			if(!cliente.getId().equals(id)) {
				return false;
			}
		}
		
		return true;
	}
}
