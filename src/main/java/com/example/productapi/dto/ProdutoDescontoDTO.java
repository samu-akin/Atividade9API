package com.example.productapi.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Data 
@NoArgsConstructor 
@AllArgsConstructor 
public class ProdutoDescontoDTO {
    private String nome;
    private BigDecimal precoOriginal; 
    private String descontoAplicado; 
    private BigDecimal precoFinal;
}
