package com.example.productapi.repository;

import com.example.productapi.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository // repositório Spring
public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    Optional<Produto> findByNomeIgnoreCase(String nome);

    List<Produto> findByNomeContainingIgnoreCase(String nome);
  
    List<Produto> findByCategoriaId(Long categoriaId);
}
