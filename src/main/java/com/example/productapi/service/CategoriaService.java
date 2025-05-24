package com.example.productapi.service;

import com.example.productapi.dto.CategoriaDTO;
import com.example.productapi.exception.BusinessRuleException;
import com.example.productapi.exception.ResourceNotFoundException;
import com.example.productapi.model.Categoria;
import com.example.productapi.repository.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service 
public class CategoriaService {

    @Autowired
    private CategoriaRepository categoriaRepository;

    private CategoriaDTO toDTO(Categoria categoria) {
        return new CategoriaDTO(categoria.getId(), categoria.getNome());
    }

    private Categoria toEntity(CategoriaDTO categoriaDTO) {
        Categoria categoria = new Categoria();
        categoria.setId(categoriaDTO.getId());
        categoria.setNome(categoriaDTO.getNome());
        return categoria;
    }

    @Transactional 
    public CategoriaDTO criarCategoria(CategoriaDTO categoriaDTO) {
        // Regra de negócio: Nome da categoria não pode ser duplicado
        if (categoriaRepository.findByNomeIgnoreCase(categoriaDTO.getNome()).isPresent()) {
            throw new BusinessRuleException("Nome já utilizado  '" + categoriaDTO.getNome() + "'.");
        }
        Categoria categoria = toEntity(categoriaDTO);
        return toDTO(categoriaRepository.save(categoria));
    }

    @Transactional(readOnly = true) 
    public List<CategoriaDTO> listarTodasCategorias() {
        return categoriaRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CategoriaDTO buscarCategoriaPorId(Long id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não localiza com ID: " + id));
        return toDTO(categoria);
    }

    @Transactional
    public CategoriaDTO atualizarCategoria(Long id, CategoriaDTO categoriaDTO) {
        Categoria categoriaExistente = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não localiza com ID: " + id));

        //  Nome da categoria não pode ser duplicado
        if (categoriaRepository.findByNomeIgnoreCase(categoriaDTO.getNome()).isPresent() &&
            !categoriaRepository.findByNomeIgnoreCase(categoriaDTO.getNome()).get().getId().equals(id)) {
            throw new BusinessRuleException("Nome já utilizado '" + categoriaDTO.getNome() + "'.");
        }

        categoriaExistente.setNome(categoriaDTO.getNome());
        return toDTO(categoriaRepository.save(categoriaExistente));
    }

    @Transactional
    public void deletarCategoria(Long id) {
        if (!categoriaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Categoria não localizada com ID: " + id);
        }
        categoriaRepository.deleteById(id);
    }
}
