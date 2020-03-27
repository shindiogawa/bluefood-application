package com.application.ogawadev.domain.restaurante;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.springframework.web.multipart.MultipartFile;

import com.application.ogawadev.application.util.FileType;
import com.application.ogawadev.application.util.StringUtils;
import com.application.ogawadev.domain.usuario.Usuario;
import com.application.ogawadev.infrastructure.web.validator.UploadConstraint;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@SuppressWarnings("serial")
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@Entity
@Table(name = "restaurante")
public class Restaurante extends Usuario{
	
	@NotBlank(message = "O CNPJ pode ser vazio")
	@Pattern(regexp = "[0-9]{14}", message = "O CNPJ possui formato inv�lido")
	@Column(length = 14, nullable = false)
	private String cnpj;
	
	@Size(max = 80)
	private String logotipo;
	
	@UploadConstraint(acceptedTypes = {FileType.PNG, FileType.JPG}, message = "O arquivo nao e um arquivo de imagem valido")
	private transient MultipartFile logotipoFile;
	
	@NotNull(message = "A taxa de entrega nao pode ser vazia")
	@Min(0)
	@Max(99)
	private BigDecimal taxaEntrega;
	
	@NotNull(message = "O tempo de entrega nao pode ser vazio")
	@Min(0)
	@Max(120)
	private Integer tempoEntregaBase;
	
	
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(
			name = "restaurante_has_categoria",
			joinColumns = @JoinColumn(name = "restaurante_id"),
			inverseJoinColumns = @JoinColumn(name = "categoria_restaurante_id")
	)
	@Size(min = 1, message = "O restaurante precisa de pelo menos uma categoria")
	@ToString.Exclude
	private Set<CategoriaRestaurante> categorias = new HashSet<>(0);
	
	@OneToMany(mappedBy = "restaurante")
	private Set<ItemCardapio> itensCardapio = new HashSet<>(0);
	
	
	public void  setLogotipoFileName() {
		
		if( getId() == null)  {
			throw new IllegalStateException("E preciso primeiro gravar o registro");
		}
		
	
		
		this.logotipo = String.format("%04d-logo.%s", getId(), FileType.of(logotipoFile.getContentType()).getExtension());
	}
	
	public String getCategoriasAsText() {
		
		Set<String> strings = new LinkedHashSet<String>();
		
		for(CategoriaRestaurante categoria : categorias) {
			
			strings.add(categoria.getNome());
			
		}
		
		return StringUtils.concatenate(strings);
	}
	
	public Integer calcularTempoEntrega( String cep ) {
		
		int soma = 0;
		
		for(char c : cep.toCharArray()) {
			
			int v = Character.getNumericValue(c);
			
			if( v >  0 ) {
				soma += v;
			}
		}
		soma /= 2;
		return tempoEntregaBase + soma;
		
	}
	
	
}
