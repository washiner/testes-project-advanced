package com.washiner.testes.controller;

import com.washiner.testes.dto.ProdutoRequest;
import com.washiner.testes.dto.ProdutoResponse;
import com.washiner.testes.service.ProdutoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController                    // marca como controller REST
@RequestMapping("/v1/produtos")    // rota base
@RequiredArgsConstructor           // injeção de dependência pelo construtor
public class ProdutoController {

    private final ProdutoService service;

    // POST /v1/produtos — cria um produto
    @PostMapping
    public ResponseEntity<ProdutoResponse> criar(@RequestBody ProdutoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(request));
    }

    // GET /v1/produtos — lista todos
    @GetMapping
    public ResponseEntity<List<ProdutoResponse>> listarTodos() {
        return ResponseEntity.ok(service.listarTodos());
    }

    // GET /v1/produtos/{id} — busca por id
    @GetMapping("/{id}")
    public ResponseEntity<ProdutoResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    // PUT /v1/produtos/{id} — atualiza
    @PutMapping("/{id}")
    public ResponseEntity<ProdutoResponse> atualizar(@PathVariable Long id, @RequestBody ProdutoRequest request) {
        return ResponseEntity.ok(service.atualizar(id, request));
    }

    // DELETE /v1/produtos/{id} — deleta
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}