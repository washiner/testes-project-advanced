package com.washiner.testes.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data                          // gera getters, setters, toString, equals, hashCode
@NoArgsConstructor             // gera construtor vazio
@AllArgsConstructor            // gera construtor com todos os campos
@Entity                        // marca como entidade JPA (vai virar tabela)
@Table(name = "produtos")      // nome da tabela no banco
public class Produto {

    @Id                                                    // chave primária
    @GeneratedValue(strategy = GenerationType.IDENTITY)   // auto incremento
    private Long id;

    @Column(nullable = false)          // coluna obrigatória
    private String nome;

    @Column(nullable = false)          // coluna obrigatória
    private BigDecimal preco;

    @Column(nullable = false)
    private String descricao; // campo sensível — não vai aparecer no response
}