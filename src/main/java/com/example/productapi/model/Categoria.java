package com.example.productapi.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Entity 
@Table(name = "categorias") 
@Data 
@NoArgsConstructor // construtor sem argumentos 
@AllArgsConstructor // construtor com argumentos 
public class Categoria {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private Long id;

    @Column(nullable = false, unique = true)
    private String nome;
  
    @OneToMany(mappedBy = "categoria", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Produto> produtos; 
}
