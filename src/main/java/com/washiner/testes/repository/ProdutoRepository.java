package com.washiner.testes.repository;

import com.washiner.testes.entity.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository  // marca como repositório Spring
public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    // JpaRepository já nos dá prontos:
    // save(), findById(), findAll(), deleteById(), existsById()
    // não precisamos escrever nada aqui por enquanto
}
