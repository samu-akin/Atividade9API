package com.example.productapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor 
@AllArgsConstructor 
public class CategoriaDTO {
    private Long id; 

    @NotBlank(message = "Não pode estar em branco") 
    @Size(min = 3, max = 50, message = "Apenas entre 3 e 50 letras")
    private String nome;
}
