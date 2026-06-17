package com.washiner.testes.dto;

import java.math.BigDecimal;

public record ProdutoResponse(
        Long id,
        String nome,
        BigDecimal preco
) {}
