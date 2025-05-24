package com.example.productapi.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Entity 
@Table(name = "produtos") 
@Data 
@NoArgsConstructor // Gera construtor sem argumentos 
@AllArgsConstructor // Gera construtor com argumentos 
public class Produto {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private Long id;

    @Column(nullable = false, unique = true) 
    private String nome;

    @Column(nullable = false) 
    private BigDecimal preco;
    // nullable = false: Um produto não pode ser salvo sem uma categoria.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", nullable = false) 
    private Categoria categoria;
}
