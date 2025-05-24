
package com.example.productapi.controller;

import com.example.productapi.dto.CategoriaDTO;
import com.example.productapi.dto.ProdutoDTO;
import com.example.productapi.service.CategoriaService;
import com.example.productapi.service.ProdutoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController 
@RequestMapping("/api/categorias") 
@Tag(name = "Categorias", description = "Gerenciamento de categorias de produtos") 
public class CategoriaController {

    @Autowired
    private CategoriaService categoriaService;

    @Autowired 
    private ProdutoService produtoService;

    @Operation(summary = "Cria uma nova categoria",
               description = "Cria nova categoria.",
               responses = {
                   @ApiResponse(responseCode = "201", description = "Categoria criada com sucesso",
                                content = @Content(mediaType = "application/json", schema = @Schema(implementation = CategoriaDTO.class))),
                   @ApiResponse(responseCode = "400", description = "nome de categoria duplicado",
                                content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"timestamp\":\"...\",\"message\":\"Já existe uma categoria com este nome.\",\"details\":\"...\"}")))
               })
    @PostMapping
    public ResponseEntity<CategoriaDTO> criarCategoria(@Valid @RequestBody CategoriaDTO categoriaDTO) {
        CategoriaDTO novaCategoria = categoriaService.criarCategoria(categoriaDTO);
        return new ResponseEntity<>(novaCategoria, HttpStatus.CREATED);
    }

    @Operation(summary = "Lista todas as categorias",
               description = "Retorna lista de todas as categorias de produtos que estao cadastrados.",
               responses = {
                   @ApiResponse(responseCode = "200", description = "Lista de categorias retornada",
                                content = @Content(mediaType = "application/json", schema = @Schema(implementation = CategoriaDTO.class)))
               })
    @GetMapping
    public ResponseEntity<List<CategoriaDTO>> listarTodasCategorias() {
        List<CategoriaDTO> categorias = categoriaService.listarTodasCategorias();
        return ResponseEntity.ok(categorias);
    }

    @Operation(summary = "Busca uma categoria por ID",
               description = "Retorna categoria pelo seu ID.",
               responses = {
                   @ApiResponse(responseCode = "200", description = "Categoria encontrada com sucesso",
                                content = @Content(mediaType = "application/json", schema = @Schema(implementation = CategoriaDTO.class))),
                   @ApiResponse(responseCode = "404", description = "Categoria não encontrada",
                                content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"timestamp\":\"...\",\"message\":\"Categoria não encontrada com ID: 1\",\"details\":\"...\"}")))
               })
    @GetMapping("/{id}")
    public ResponseEntity<CategoriaDTO> buscarCategoriaPorId(
            @Parameter(description = "Buscar categoria pelo ID", required = true)
            @PathVariable Long id) {
        CategoriaDTO categoria = categoriaService.buscarCategoriaPorId(id);
        return ResponseEntity.ok(categoria);
    }

    @Operation(summary = "Atualiza uma categoria existente",
               description = "Atualiza as infos de uma categoria pelo ID.",
               responses = {
                   @ApiResponse(responseCode = "200", description = "Categoria atualizada com sucesso",
                                content = @Content(mediaType = "application/json", schema = @Schema(implementation = CategoriaDTO.class))),
                   @ApiResponse(responseCode = "400", description = "Pedido inválido",
                                content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"timestamp\":\"...\",\"message\":\"Já tem outra categoria com este nome.\",\"details\":\"...\"}"))),
                   @ApiResponse(responseCode = "404", description = "Categoria não encontrada",
                                content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"timestamp\":\"...\",\"message\":\"Categoria não encontrada com ID: 1\",\"details\":\"...\"}")))
               })
    @PutMapping("/{id}") 
    public ResponseEntity<CategoriaDTO> atualizarCategoria(
            @Parameter(description = "Atualiza categoria", required = true)
            @PathVariable Long id,
            @Valid @RequestBody CategoriaDTO categoriaDTO) {
        CategoriaDTO categoriaAtualizada = categoriaService.atualizarCategoria(id, categoriaDTO);
        return ResponseEntity.ok(categoriaAtualizada);
    }

    @Operation(summary = "Deleta uma categoria",
               description = "Deleta categoria de produto pelo ID.",
               responses = {
                   @ApiResponse(responseCode = "204", description = "Categoria deletada !"),
                   @ApiResponse(responseCode = "404", description = "Categoria não encontrada",
                                content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"timestamp\":\"...\",\"message\":\"Categoria não encontrada com ID: 1\",\"details\":\"...\"}")))
               })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarCategoria(
            @Parameter(description = "Deletar categoria", required = true)
            @PathVariable Long id) {
        categoriaService.deletarCategoria(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "Lista todos os produtos de uma categoria",
               description = "Retorna lista de todos os produtos de uma categoria.",
               responses = {
                   @ApiResponse(responseCode = "200", description = "Lista de produtos da categoria",
                                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProdutoDTO.class))),
                   @ApiResponse(responseCode = "404", description = "Categoria não encontrada",
                                content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"timestamp\":\"...\",\"message\":\"Categoria não encontrada com esse ID: 1\",\"details\":\"...\"}")))
               })
    @GetMapping("/{id}/produtos") // Mapeia requisições GET para /api/categorias/{id}/produtos
    public ResponseEntity<List<ProdutoDTO>> listarProdutosPorCategoria(
            @Parameter(description = "ID da categoria para listar os produtos", required = true)
            @PathVariable Long id) {
        List<ProdutoDTO> produtos = produtoService.listarProdutosPorCategoria(id);
        return ResponseEntity.ok(produtos);
    }
}
