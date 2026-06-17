package com.washiner.testes.dto;

import java.math.BigDecimal;

public record ProdutoRequest(
        String nome,
        BigDecimal preco
) {}