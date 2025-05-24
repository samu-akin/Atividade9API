package com.example.productapi.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Data 
@NoArgsConstructor
@AllArgsConstructor
public class ProdutoDTO {
    private Long id;

    @NotBlank(message = "Não pode estar em branco") 
    @Size(min = 3, max = 100, message = "Apenas entre 3 e 100 letras")
    private String nome;

    @NotNull(message = "Não pode estar em branco")
    @DecimalMax(value = "10000.00", inclusive = true, message = "O preço não pode ser maior que R$ 10.000,00")
    private BigDecimal preco;

    @NotNull(message = "A categoria do produto não pode estar em branco")
    private Long categoriaId;
}
