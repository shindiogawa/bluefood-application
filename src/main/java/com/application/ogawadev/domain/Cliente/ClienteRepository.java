package com.application.ogawadev.domain.Cliente;



import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Integer> {
	
	public Cliente findByEmail(String email);


}
