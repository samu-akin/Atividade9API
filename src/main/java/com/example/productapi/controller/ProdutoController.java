package com.example.productapi.controller;

import com.example.productapi.dto.ProdutoDTO;
import com.example.productapi.dto.ProdutoDescontoDTO;
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

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/produtos")
@Tag(name = "Produtos", description = "Gerenciamento de produtos") 
public class ProdutoController {

    @Autowired 
    private ProdutoService produtoService;

    @Operation(summary = "Cria um novo produto",
               description = "Cria um novo produto com as informações especificadas.",
               responses = {
                   @ApiResponse(responseCode = "201", description = "Produto criado !",
                                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProdutoDTO.class))),
                   @ApiResponse(responseCode = "400", description = "regras de negócio violadas ou nome duplicado",
                                content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"timestamp\":\"...\",\"message\":\"Nome do produto já existe.\",\"details\":\"...\"}"))),
                   @ApiResponse(responseCode = "404", description = "Categoria não encontrada",
                                content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"timestamp\":\"...\",\"message\":\"Categoria não encontrada com ID: 1\",\"details\":\"...\"}")))
               })
    @PostMapping
    public ResponseEntity<ProdutoDTO> criarProduto(@Valid @RequestBody ProdutoDTO produtoDTO) {
        ProdutoDTO novoProduto = produtoService.criarProduto(produtoDTO);
        return new ResponseEntity<>(novoProduto, HttpStatus.CREATED);
    }

    @Operation(summary = "Atualiza um produto existente",
               description = "Atualiza infos de um produto pelo ID.",
               responses = {
                   @ApiResponse(responseCode = "200", description = "Produto atualizado !",
                                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProdutoDTO.class))),
                   @ApiResponse(responseCode = "400", description = "regras de negócio violadas ou nome duplicado",
                                content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"timestamp\":\"...\",\"message\":\"Nome do produto já existe.\",\"details\":\"...\"}"))),
                   @ApiResponse(responseCode = "404", description = "Produto ou Categoria não encontrada",
                                content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"timestamp\":\"...\",\"message\":\"Produto não encontrado com ID: 1\",\"details\":\"...\"}")))
               })
    @PutMapping("/{id}") 
    public ResponseEntity<ProdutoDTO> atualizarProduto(
            @Parameter(description = "atualiza produto", required = true)
            @PathVariable Long id,
            @Valid @RequestBody ProdutoDTO produtoDTO) {
        ProdutoDTO produtoAtualizado = produtoService.atualizarProduto(id, produtoDTO);
        return ResponseEntity.ok(produtoAtualizado);
    }

    @Operation(summary = "Lista todos os produtos",
               description = "Retorna lista dos produtos cadastrados.",
               responses = {
                   @ApiResponse(responseCode = "200", description = "Lista de produtos retornada !",
                                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProdutoDTO.class)))
               })
    @GetMapping
    public ResponseEntity<List<ProdutoDTO>> listarTodosProdutos() {
        List<ProdutoDTO> produtos = produtoService.listarTodosProdutos();
        return ResponseEntity.ok(produtos);
    }

    @Operation(summary = "Busca um produto por ID",
               description = "Retorna produto pelo ID.",
               responses = {
                   @ApiResponse(responseCode = "200", description = "Produto encontrado !",
                                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProdutoDTO.class))),
                   @ApiResponse(responseCode = "404", description = "Produto não encontrado",
                                content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"timestamp\":\"...\",\"message\":\"Produto não encontrado com ID: 1\",\"details\":\"...\"}")))
               })
    @GetMapping("/{id}")
    public ResponseEntity<ProdutoDTO> buscarProdutoPorId(
            @Parameter(description = "Busca produto pelo id dele", required = true)
            @PathVariable Long id) {
        ProdutoDTO produto = produtoService.buscarProdutoPorId(id);
        return ResponseEntity.ok(produto);
    }

    @Operation(summary = "Busca produtos por nome",
               description = "Lista de produtos com o nome pesquisado. Retorna lista vazia se tiver nada.",
               responses = {
                   @ApiResponse(responseCode = "200", description = "Lista de produtos retornada !",
                                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProdutoDTO.class)))
               })
    @GetMapping("/buscar")
    public ResponseEntity<List<ProdutoDTO>> buscarProdutosPorNome(
            @Parameter(description = "Busca o produto pelo nome", required = true)
            @RequestParam String nome) {
        List<ProdutoDTO> produtos = produtoService.buscarProdutosPorNome(nome);
        return ResponseEntity.ok(produtos);
    }

    @Operation(summary = "Calcula preço de produto com desconto",
               description = "Retorna o valor final de um produto com um desconto percentual aplicado.",
               responses = {
                   @ApiResponse(responseCode = "200", description = "Preço com desconto calculado !",
                                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProdutoDescontoDTO.class))),
                   @ApiResponse(responseCode = "400", description = "Percentual de desconto inválido",
                                content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"timestamp\":\"...\",\"message\":\"O desconto deve ser entre 0 e 50%.\",\"details\":\"...\"}"))),
                   @ApiResponse(responseCode = "404", description = "Produto não encontrado",
                                content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"timestamp\":\"...\",\"message\":\"Produto não encontrado com ID: 1\",\"details\":\"...\"}")))
               })
    @GetMapping("/{id}/desconto") 
    public ResponseEntity<ProdutoDescontoDTO> calcularPrecoComDesconto(
            @Parameter(description = "ID do produto", required = true)
            @PathVariable Long id,
            @Parameter(description = "desconto que sera aplicado (0-50)", required = true)
            @RequestParam BigDecimal percentual) {
        ProdutoDescontoDTO produtoComDesconto = produtoService.calcularPrecoComDesconto(id, percentual);
        return ResponseEntity.ok(produtoComDesconto);
    }

    @Operation(summary = "Deleta um produto",
               description = "Deleta um produto pelo ID.",
               responses = {
                   @ApiResponse(responseCode = "204", description = "Produto deletado"),
                   @ApiResponse(responseCode = "404", description = "Produto não encontrado",
                                content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"timestamp\":\"...\",\"message\":\"Produto não encontrado com ID: 1\",\"details\":\"...\"}")))
               })
    @DeleteMapping("/{id}") 
    public ResponseEntity<Void> deletarProduto(
            @Parameter(description = "Deletar produto", required = true)
            @PathVariable Long id) {
        produtoService.deletarProduto(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
