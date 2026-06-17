package com.washiner.testes.dto;

import java.math.BigDecimal;

public record ProdutoRequest(
        String nome,
        BigDecimal preco,
        String descricao  // cliente manda a descrição
) {}