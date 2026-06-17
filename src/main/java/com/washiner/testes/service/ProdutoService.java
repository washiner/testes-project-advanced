package com.washiner.testes.service;

import com.washiner.testes.dto.ProdutoRequest;
import com.washiner.testes.dto.ProdutoResponse;
import com.washiner.testes.entity.Produto;
import com.washiner.testes.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service                // marca como serviço Spring
@RequiredArgsConstructor // gera construtor com os campos final (injeção de dependência)
public class ProdutoService {

    private final ProdutoRepository repository; // injetado pelo construtor

    // CRIAR
    public ProdutoResponse criar(ProdutoRequest request) {
        Produto produto = new Produto(null, request.nome(), request.preco(), request.descricao()); // monta a entidade
        Produto salvo = repository.save(produto);                             // salva no banco
        return new ProdutoResponse(salvo.getId(), salvo.getNome(), salvo.getPreco()); // devolve o response
    }

    // LISTAR TODOS
    public List<ProdutoResponse> listarTodos() {
        return repository.findAll()                                           // busca todos no banco
                .stream()                                                     // transforma em stream
                .map(p -> new ProdutoResponse(p.getId(), p.getNome(), p.getPreco())) // converte cada um
                .toList();                                                    // coleta em lista
    }

    // BUSCAR POR ID
    public ProdutoResponse buscarPorId(Long id) {
        Produto produto = repository.findById(id)                            // busca no banco
                .orElseThrow(() -> new RuntimeException("Produto não encontrado")); // lança exceção se não achar
        return new ProdutoResponse(produto.getId(), produto.getNome(), produto.getPreco());
    }

    // ATUALIZAR
    public ProdutoResponse atualizar(Long id, ProdutoRequest request) {
        Produto produto = repository.findById(id)                            // busca no banco
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));
        produto.setNome(request.nome());                                     // atualiza o nome
        produto.setPreco(request.preco());                                   // atualiza o preço
        produto.setDescricao(request.descricao());
        Produto salvo = repository.save(produto);                            // salva as alterações
        return new ProdutoResponse(salvo.getId(), salvo.getNome(), salvo.getPreco());
    }

    // DELETAR
    public void deletar(Long id) {
        if (!repository.existsById(id)) {                                    // verifica se existe
            throw new RuntimeException("Produto não encontrado");            // lança exceção se não achar
        }
        repository.deleteById(id);                                           // deleta do banco
    }
}
