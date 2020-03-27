package com.application.ogawadev.domain.usuario;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.application.ogawadev.application.util.StringUtils;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@SuppressWarnings("serial")
@MappedSuperclass
public class Usuario implements Serializable {

	@EqualsAndHashCode.Include
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@NotBlank(message = "O nome nao pode ser vazio")
	@Size(max = 80, message = "O nome e muito grande")
	@Column(nullable = false)
	private String nome;
	
	@NotBlank(message = "O e-mail nao pode ser vazio")
	@Size(max = 60, message = "O e-mail e muito grande")
	@Email(message = "O e-mail e invalido")
	@Column(nullable = false)
	private String email;
	
	@NotBlank(message = "A senha nao pode ser vazia")
	@Size(max = 80, message = "A senha e muito grande")
	@Column(nullable = false)
	private String senha;
	
	@NotBlank(message = "O telefone nao pode ser vazio")
	@Pattern(regexp = "[0-9]{10,11}", message = "O telefone possui formato invalido")
	@Column(length = 11, nullable = false)
	private String telefone;
	
	
	public void encryptPassword() {
		
		this.senha = StringUtils.encrypt(this.senha);
		
	}
	
}
