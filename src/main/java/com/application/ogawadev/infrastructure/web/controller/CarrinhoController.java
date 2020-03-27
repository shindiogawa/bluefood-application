package com.application.ogawadev.infrastructure.web.controller;

import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import com.application.ogawadev.application.service.ImageService;
import com.application.ogawadev.domain.pedido.Carrinho;
import com.application.ogawadev.domain.pedido.ItemPedido;
import com.application.ogawadev.domain.pedido.Pedido;
import com.application.ogawadev.domain.pedido.PedidoRepository;
import com.application.ogawadev.domain.pedido.RestauranteDiferenteException;
import com.application.ogawadev.domain.restaurante.ItemCardapio;
import com.application.ogawadev.domain.restaurante.ItemCardapioRepository;

@Controller
@RequestMapping("/cliente/carrinho")
@SessionAttributes("carrinho")
public class CarrinhoController {
	
	@Autowired
	private ItemCardapioRepository itemCardapioRepository;
	
	@Autowired
	private PedidoRepository pedidoRepository;
	
	@ModelAttribute("carrinho")
	public Carrinho carrinho() {
		return new Carrinho();
	}
	
	@GetMapping(path = "/adicionar")
	public String adicionarItem(
			@RequestParam("itemId") Integer itemId,
			@RequestParam("quantidade") Integer quantidade,
			@RequestParam("observacoes") String observacoes,
			@ModelAttribute("carrinho") Carrinho carrinho,
			Model model) {
		
		
		
		ItemCardapio itemCardapio = itemCardapioRepository.findById(itemId).orElseThrow(NoSuchElementException::new);
		
		try {
			
			carrinho.adicionarItem(itemCardapio, quantidade, observacoes);
			
		} catch (RestauranteDiferenteException e) {
			model.addAttribute("msg", "Nao e possivel misturar comidas de restaurantes diferentes");
		}
		
		
		return "cliente-carrinho";
		
	}
	
	@GetMapping(path = "/visualizar")
	public String viewCarrinho() {
		return "cliente-carrinho";
	}
	
	@GetMapping(path = "/remover")
	public String removerItem(
			@RequestParam("itemId") Integer itemId,
			@ModelAttribute("carrinho") Carrinho carrinho,
			SessionStatus sessionStatus,
			Model model) {
		
		ItemCardapio itemCardapio = itemCardapioRepository.findById(itemId).orElseThrow(NoSuchElementException::new);
		
		carrinho.removerItem(itemCardapio);
		
		if(carrinho.vazio()) {
			
			sessionStatus.setComplete();
			
		}
		
		return "cliente-carrinho";
		
	}
	
	@GetMapping(path = "/refazerCarrinho")
	public String refazerCarrinho(
			@RequestParam("pedidoId") Integer pedidoId,
			@ModelAttribute("carrinho") Carrinho carrinho,
			Model model) {
		
		Pedido pedido = pedidoRepository.findById(pedidoId).orElseThrow(NoSuchElementException::new);
		
		carrinho.limpar();
		
		for(ItemPedido itemPedido : pedido.getItens()) {
			
			carrinho.adicionarItem(itemPedido);
			
		}
		
		return "cliente-carrinho";
	}
	
}
