package com.application.ogawadev.infrastructure.web.controller;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.ui.Model;

import com.application.ogawadev.domain.restaurante.CategoriaRestaurante;
import com.application.ogawadev.domain.restaurante.CategoriaRestauranteRepository;

public class ControllerHelper {
	
	public static void setEditMode(Model model, boolean isEdit) {
		
		model.addAttribute("editMode", isEdit);
		
	}
	
	public static void addCategoriasToRequest(CategoriaRestauranteRepository repository, Model model) {

		List<CategoriaRestaurante> categorias  = repository.findAll(Sort.by("nome"));
		model.addAttribute("categorias", categorias);
	}

}
