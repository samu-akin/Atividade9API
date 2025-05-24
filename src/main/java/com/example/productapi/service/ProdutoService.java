package com.example.productapi.service;

import com.example.productapi.dto.ProdutoDTO;
import com.example.productapi.dto.ProdutoDescontoDTO;
import com.example.productapi.exception.BusinessRuleException;
import com.example.productapi.exception.ResourceNotFoundException;
import com.example.productapi.model.Categoria;
import com.example.productapi.model.Produto;
import com.example.productapi.repository.CategoriaRepository;
import com.example.productapi.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service //
public class ProdutoService {

    @Autowired 
    private ProdutoRepository produtoRepository;

    @Autowired 
    private CategoriaRepository categoriaRepository;

    private ProdutoDTO toDTO(Produto produto) {
        return new ProdutoDTO(produto.getId(), produto.getNome(), produto.getPreco(), produto.getCategoria().getId());
    }

    private Produto toEntity(ProdutoDTO produtoDTO, Categoria categoria) {
        Produto produto = new Produto();
        produto.setId(produtoDTO.getId());
        produto.setNome(produtoDTO.getNome());
        produto.setPreco(produtoDTO.getPreco());
        produto.setCategoria(categoria);
        return produto;
    }

    @Transactional 
    public ProdutoDTO criarProduto(ProdutoDTO produtoDTO) {
       
        validarRegrasDeNegocio(produtoDTO.getNome(), produtoDTO.getPreco(), null); 

        // Regra de negócio: Nome do produto não pode ser duplicado
        if (produtoRepository.findByNomeIgnoreCase(produtoDTO.getNome()).isPresent()) {
            throw new BusinessRuleException("Já existe um produto com o nome '" + produtoDTO.getNome() + "'.");
        }

        // Regra de negócio: Não permitir salvar produto sem categoria
        Categoria categoria = categoriaRepository.findById(produtoDTO.getCategoriaId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada com ID: " + produtoDTO.getCategoriaId()));

        Produto produto = toEntity(produtoDTO, categoria);
        return toDTO(produtoRepository.save(produto));
    }

    @Transactional 
    public ProdutoDTO atualizarProduto(Long id, ProdutoDTO produtoDTO) {
        Produto produtoExistente = produtoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com ID: " + id));

        validarRegrasDeNegocio(produtoDTO.getNome(), produtoDTO.getPreco(), id);

        // Regra de negócio: Nome do produto não pode ser duplicado
        if (produtoRepository.findByNomeIgnoreCase(produtoDTO.getNome()).isPresent() &&
            !produtoRepository.findByNomeIgnoreCase(produtoDTO.getNome()).get().getId().equals(id)) {
            throw new BusinessRuleException("Já existe outro produto com o nome '" + produtoDTO.getNome() + "'.");
        }

        // Regra de negócio: A categoria deve ser escolhida por ID
        Categoria categoria = categoriaRepository.findById(produtoDTO.getCategoriaId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada com ID: " + produtoDTO.getCategoriaId()));

        produtoExistente.setNome(produtoDTO.getNome());
        produtoExistente.setPreco(produtoDTO.getPreco());
        produtoExistente.setCategoria(categoria); 

        return toDTO(produtoRepository.save(produtoExistente));
    }

    @Transactional(readOnly = true) 
    public List<ProdutoDTO> listarTodosProdutos() {
        return produtoRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProdutoDTO buscarProdutoPorId(Long id) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não localizado com ID: " + id));
        return toDTO(produto);
    }

    @Transactional(readOnly = true)
    public List<ProdutoDTO> buscarProdutosPorNome(String nome) {
        // Se nada for encontrado, retornar lista vazia pra n lançar erro
        return produtoRepository.findByNomeContainingIgnoreCase(nome).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProdutoDescontoDTO calcularPrecoComDesconto(Long id, BigDecimal percentual) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com ID: " + id));

        // Regra de negócio: Não permitir descontos maiores que 50%.
        if (percentual.compareTo(BigDecimal.valueOf(50)) > 0 || percentual.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessRuleException("O percentual de desconto deve ser entre 0 e 50%.");
        }

        BigDecimal descontoFator = percentual.divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        BigDecimal valorDesconto = produto.getPreco().multiply(descontoFator);
        BigDecimal precoFinal = produto.getPreco().subtract(valorDesconto).setScale(2, RoundingMode.HALF_UP);

        return new ProdutoDescontoDTO(
                produto.getNome(),
                produto.getPreco(),
                percentual.stripTrailingZeros().toPlainString() + "%", 
                precoFinal
        );
    }

    @Transactional(readOnly = true)
    public List<ProdutoDTO> listarProdutosPorCategoria(Long categoriaId) {
        
        if (!categoriaRepository.existsById(categoriaId)) {
            throw new ResourceNotFoundException("Categoria não encontrada com ID: " + categoriaId);
        }
        return produtoRepository.findByCategoriaId(categoriaId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deletarProduto(Long id) {
        if (!produtoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Produto não encontrado com ID: " + id);
        }
        produtoRepository.deleteById(id);
    }

  
    private void validarRegrasDeNegocio(String nome, BigDecimal preco, Long currentProductId) {

        if (nome.toLowerCase().contains("promoção") && preco.compareTo(BigDecimal.valueOf(500)) >= 0) {
            throw new BusinessRuleException("Produtos em 'Promoção' deve ter preço menor que R$ 500,00.");
        }
    }
}
