package com.application.ogawadev.infrastructure.web.controller;

import java.util.List;
import java.util.NoSuchElementException;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.application.ogawadev.application.service.PedidoService;
import com.application.ogawadev.application.service.RelatorioService;
import com.application.ogawadev.application.service.RestauranteService;
import com.application.ogawadev.application.service.ValidationException;
import com.application.ogawadev.application.util.SecurityUtils;
import com.application.ogawadev.domain.pedido.Pedido;
import com.application.ogawadev.domain.pedido.PedidoRepository;
import com.application.ogawadev.domain.pedido.RelatorioItemFaturamento;
import com.application.ogawadev.domain.pedido.RelatorioItemFilter;
import com.application.ogawadev.domain.pedido.RelatorioPedidoFilter;
import com.application.ogawadev.domain.restaurante.CategoriaRestauranteRepository;
import com.application.ogawadev.domain.restaurante.ItemCardapio;
import com.application.ogawadev.domain.restaurante.ItemCardapioRepository;
import com.application.ogawadev.domain.restaurante.Restaurante;
import com.application.ogawadev.domain.restaurante.RestauranteRepository;

@Controller
@RequestMapping(path = "/restaurante")
public class RestauranteController {
	
	@Autowired
	RestauranteRepository restauranteRepository;
	
	@Autowired
	RestauranteService restauranteService;
	
	@Autowired
	private CategoriaRestauranteRepository categoriaRestauranteRepository;
	
	@Autowired
	private ItemCardapioRepository itemCardapioRepository;
	
	@Autowired
	private PedidoRepository pedidoRepository;
	
	@Autowired
	private RelatorioService relatorioService;
	
	@GetMapping(path = "/home")
	public String home(Model model) {
		
		Integer restauranteId = SecurityUtils.loggedRestaurante().getId();
		List<Pedido> pedidos = pedidoRepository.findByRestaurante_IdOrderByDataDesc(restauranteId);
		model.addAttribute("pedidos", pedidos);
		return "restaurante-home";
	}
	
	@GetMapping("/edit")
	public String edit(Model model) {
		
		Integer restauranteId = SecurityUtils.loggedRestaurante().getId();
		
		Restaurante restaurante = restauranteRepository.findById(restauranteId).orElseThrow(NoSuchElementException::new);
		
		
		model.addAttribute("restaurante", restaurante);
		
		ControllerHelper.setEditMode(model, true);
		ControllerHelper.addCategoriasToRequest(categoriaRestauranteRepository, model);
		return "restaurante-cadastro";
		
	}
	
	@PostMapping(path="/save")
	public String saveCliente(@ModelAttribute("restaurante") @Valid Restaurante restaurante,
			Errors errors,
			Model model) {
			
		
		if (!errors.hasErrors()) {
			
			try{
				
				restauranteService.saveRestaurante(restaurante);
				model.addAttribute("msg", "Restaurante gravado com sucesso!");
			} catch (ValidationException e) {
				
				errors.rejectValue("email", null , e.getMessage());
				
			}
			
			
		}
		
		ControllerHelper.setEditMode(model, true);
		ControllerHelper.addCategoriasToRequest(categoriaRestauranteRepository, model);
		return "restaurante-cadastro";
		
	}
	
	@GetMapping(path = "/comidas")
	public String viewComidas(Model model) {
		Integer restauranteId = SecurityUtils.loggedRestaurante().getId();
		
		Restaurante restaurante = restauranteRepository.findById(restauranteId).orElseThrow(NoSuchElementException::new);
		
		List<ItemCardapio> itensCardapio = itemCardapioRepository.findByRestaurante_IdOrderByNome(restaurante.getId());
		
		model.addAttribute("itensCardapio", itensCardapio);
		model.addAttribute("restaurante", restaurante);
		model.addAttribute("itemCardapio", new ItemCardapio());
		
		return "restaurante-comidas";
	}
	
	@GetMapping(path = "/comidas/remover")
	public String remover(@RequestParam("itemId")Integer itemId, Model model) {
		
		itemCardapioRepository.deleteById(itemId);
		
		return "redirect:/restaurante/comidas";
	}
	
	@PostMapping(path = "/comidas/cadastrar")
	public String cadastrar(
			@Valid @ModelAttribute("itemCardapio") ItemCardapio itemCardapio,
			Errors errors,
			Model model) {
		
		if(errors.hasErrors()) {
			
			Integer restauranteId = SecurityUtils.loggedRestaurante().getId();
			
			Restaurante restaurante = restauranteRepository.findById(restauranteId).orElseThrow(NoSuchElementException::new);
			
			List<ItemCardapio> itensCardapio = itemCardapioRepository.findByRestaurante_IdOrderByNome(restaurante.getId());
			
			model.addAttribute("itensCardapio", itensCardapio);
			
			return "restaurante-comidas";
		}
		
		restauranteService.saveItemCardapio(itemCardapio);
		
		return "redirect:/restaurante/comidas";
	}
	
	@GetMapping(path = "/pedido")
	public String viewPedido(@RequestParam("pedidoId") Integer pedidoId,
			Model model) {
		
		Pedido pedido = pedidoRepository.findById(pedidoId).orElseThrow(NoSuchElementException::new);
		model.addAttribute("pedido",pedido);
		return "restaurante-pedido";
	}
	
	
	@PostMapping(path = "/pedido/proximoStatus")
	public String proximoStatus(@RequestParam("pedidoId") Integer pedidoId,
			Model model) {
		
		Pedido pedido = pedidoRepository.findById(pedidoId).orElseThrow(NoSuchElementException::new);
		
		pedido.definirProximoStatus();
		
		pedidoRepository.save(pedido);
		model.addAttribute("pedido",pedido);
		model.addAttribute("msg", "Status alterado com sucesso");
		
		return "restaurante-pedido";
		
	}
	
	@GetMapping("/relatorio/pedidos")
	public String relatorioPedidos(
			@ModelAttribute("relatorioPedidoFilter") RelatorioPedidoFilter filter,
			Model model
			) {
		
		Integer restauranteId = SecurityUtils.loggedRestaurante().getId();
		
		List<Pedido> pedidos = relatorioService.listPedidos(restauranteId, filter);
		
		model.addAttribute("pedidos", pedidos);
		model.addAttribute("filter", filter);
		
		return "restaurante-relatorio-pedidos";
		
	}
	
	@GetMapping("/relatorio/itens")
	public String relatorioItens(
			@ModelAttribute("relatorioItemFilter") RelatorioItemFilter filter,
			Model model
			) {
		
		Integer restauranteId = SecurityUtils.loggedRestaurante().getId();
		List<ItemCardapio> itensCardapio = itemCardapioRepository.findByRestaurante_IdOrderByNome(restauranteId);
		model.addAttribute("itensCardapio", itensCardapio);
		
		List<RelatorioItemFaturamento> itensCalculados = relatorioService.calcularFaturamentoItens(restauranteId, filter);
		
		model.addAttribute("itensCalculados", itensCalculados);
		model.addAttribute("relatorioItemFilter", filter);
	
		
		return "restaurante-relatorio-itens";
		
	}
}
