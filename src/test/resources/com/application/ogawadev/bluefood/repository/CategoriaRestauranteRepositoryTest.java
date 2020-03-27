package com.application.ogawadev.bluefood.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.NoSuchElementException;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.application.ogawadev.domain.restaurante.CategoriaRestaurante;
import com.application.ogawadev.domain.restaurante.CategoriaRestauranteRepository;

@DataJpaTest
@ActiveProfiles("test")
public class CategoriaRestauranteRepositoryTest {
	
	@Autowired
	private EntityManager entityManager;
	
	@Test
	public void testInsertAndeDelete() throws Exception{
		
		assertThat(entityManager).isNotNull();
		
		CategoriaRestaurante cr = new CategoriaRestaurante();
		
		cr.setNome("Chinesa");
		cr.setImagem("chinesa.png");
		
		entityManager.persist(cr);
		
		assertThat(cr.getId()).isNotNull();
		
		CategoriaRestaurante cr2 = entityManager.find(CategoriaRestaurante.class, cr.getId());
		
		assertThat(cr.getNome()).isEqualTo(cr2.getNome());
		
		
		entityManager.remove(cr);
		
		assertThat(entityManager.find(CategoriaRestaurante.class, cr.getId())).isNull();
		
	}
}
