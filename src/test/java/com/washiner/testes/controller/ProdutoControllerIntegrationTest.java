package com.washiner.testes.controller;
import com.washiner.testes.dto.ProdutoRequest;
import java.math.BigDecimal;
import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import tools.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import com.washiner.testes.entity.Produto;
import com.washiner.testes.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import org.junit.jupiter.api.BeforeEach;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest              // sobe o contexto Spring inteiro pra teste
@AutoConfigureMockMvc        // configura o MockMvc pra simular requisições HTTP
public class ProdutoControllerIntegrationTest {

    @Autowired                       // injeta o MockMvc já configurado pelo Spring
    private MockMvc mockMvc;

    @Autowired                       // injeta o ObjectMapper, usado para converter objetos Java em JSON
    private ObjectMapper objectMapper;

    @Autowired
    private ProdutoRepository produtoRepository;

    @BeforeEach
    void limparBanco() {
        //Antes de CADA teste, limpamos a tabela de produtos.
        // Isso garante que cada teste comece com o banco vazio, sem interferência de testes anteriores.

        // 1. Remove todos os registros da tabela produtos antes de cada teste
        produtoRepository.deleteAll();
    }

    @Test
    void deveCriarProdutoComSucesso() throws Exception {
        //Esse teste sobe o contexto Spring inteiro e simula um POST real chegando no Controller.
        // O fluxo completo (Controller -> Service -> Repository -> H2) é executado de verdade.

        //Passo a passo:
        //
        //Cria um ProdutoRequest com os dados que o "cliente" vai enviar.
        //Converte esse objeto para uma String JSON, usando o ObjectMapper.
        //Usa o mockMvc para simular um POST para "/v1/produtos", enviando o JSON no corpo.
        //Verifica se o status retornado foi 201 Created.
        //Verifica se o JSON de resposta contém os campos esperados (nome, preco).

        //arrange - prepara o cenario

        // 1. Cria o request com os dados que serão enviados na requisição
        ProdutoRequest request = new ProdutoRequest("Teclado", new BigDecimal("299.90"), "Descrição sensível");

        // 2. Converte o objeto Java em uma String JSON
        String jsonRequest = objectMapper.writeValueAsString(request);


        //act + assert juntos aqui = porque o MockMvc já permite encadear a chamada com as verificações

        // 3. Simula um POST para /v1/produtos enviando o JSON, e encadeia as verificações da resposta
        mockMvc.perform(post("/v1/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated())                         // verifica status 201
                .andExpect(jsonPath("$.nome").value("Teclado"))           // verifica o campo nome no JSON
                .andExpect(jsonPath("$.preco").value(299.90));            // verifica o campo preco no JSON
    }

    @Test
    void deveBuscarProdutoPorIdComSucesso() throws Exception {
        //Esse teste verifica o fluxo completo de buscar um produto que já existe no banco.
        // Diferente do teste unitário, aqui o produto precisa ser INSERIDO de verdade antes da busca.

        //Passo a passo:
        //
        //Cria e salva um Produto direto no repository (simulando que ele já existe no banco).
        //Usa o mockMvc para simular um GET para "/v1/produtos/{id}", usando o id do produto salvo.
        //Verifica se o status retornado foi 200 OK.
        //Verifica se o JSON de resposta contém os dados corretos.

        //arrange - prepara o cenario

        // 1. Cria e salva um produto de verdade no banco H2, usando o repository diretamente
        Produto produtoSalvo = produtoRepository.save(new Produto(null, "Mouse", new BigDecimal("149.90"), "Descrição sensível"));


        //act + assert juntos aqui = porque o MockMvc já permite encadear a chamada com as verificações

        // 2. Simula um GET para /v1/produtos/{id}, usando o id do produto que acabamos de salvar
        mockMvc.perform(get("/v1/produtos/{id}", produtoSalvo.getId()))
                .andExpect(status().isOk())                                  // verifica status 200
                .andExpect(jsonPath("$.nome").value("Mouse"))                // verifica o campo nome
                .andExpect(jsonPath("$.preco").value(149.90));                // verifica o campo preco
    }

    @Test
    void deveListarTodosOsProdutos() throws Exception {
        //Esse teste verifica o fluxo completo de listar todos os produtos cadastrados.

        //Passo a passo:
        //
        //Cria e salva dois Produtos direto no repository.
        //Usa o mockMvc para simular um GET para "/v1/produtos" (sem id, lista tudo).
        //Verifica se o status retornado foi 200 OK.
        //Verifica se o JSON de resposta é uma lista com 2 itens.

        //arrange - prepara o cenario

        // 1. Cria e salva dois produtos de verdade no banco H2
        produtoRepository.save(new Produto(null, "Teclado", new BigDecimal("299.90"), "Descrição sensível 1"));
        produtoRepository.save(new Produto(null, "Mouse", new BigDecimal("149.90"), "Descrição sensível 2"));


        //act + assert juntos aqui = porque o MockMvc já permite encadear a chamada com as verificações

        // 2. Simula um GET para /v1/produtos, sem id, esperando a lista completa
        mockMvc.perform(get("/v1/produtos"))
                .andExpect(status().isOk())                                  // verifica status 200
                .andExpect(jsonPath("$.length()").value(2));                  // verifica que a lista tem 2 itens
    }

    @Test
    void deveAtualizarProdutoComSucesso() throws Exception {
        //Esse teste verifica o fluxo completo de atualizar um produto existente.

        //Passo a passo:
        //
        //Cria e salva um Produto direto no repository (produto que vamos atualizar).
        //Cria um ProdutoRequest com os dados novos.
        //Converte o request para JSON.
        //Usa o mockMvc para simular um PUT para "/v1/produtos/{id}".
        //Verifica se o status foi 200 OK e se os dados retornados são os atualizados.

        //arrange - prepara o cenario

        // 1. Cria e salva um produto de verdade no banco H2
        Produto produtoSalvo = produtoRepository.save(new Produto(null, "Teclado", new BigDecimal("299.90"), "Descrição original"));

        // 2. Cria o request com os dados novos para atualização
        ProdutoRequest request = new ProdutoRequest("Teclado Gamer", new BigDecimal("349.90"), "Nova descrição");

        // 3. Converte o request em JSON
        String jsonRequest = objectMapper.writeValueAsString(request);


        //act + assert juntos aqui = porque o MockMvc já permite encadear a chamada com as verificações

        // 4. Simula um PUT para /v1/produtos/{id}, enviando o JSON com os dados novos
        mockMvc.perform(put("/v1/produtos/{id}", produtoSalvo.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())                                   // verifica status 200
                .andExpect(jsonPath("$.nome").value("Teclado Gamer"))          // verifica o nome atualizado
                .andExpect(jsonPath("$.preco").value(349.90));                 // verifica o preco atualizado
    }

    @Test
    void deveDeletarProdutoComSucesso() throws Exception {
        //Esse teste verifica o fluxo completo de deletar um produto existente.

        //Passo a passo:
        //
        //Cria e salva um Produto direto no repository (produto que vamos deletar).
        //Usa o mockMvc para simular um DELETE para "/v1/produtos/{id}".
        //Verifica se o status foi 204 No Content.
        //Confirma que o produto realmente não existe mais no banco, usando o repository.

        //arrange - prepara o cenario

        // 1. Cria e salva um produto de verdade no banco H2
        Produto produtoSalvo = produtoRepository.save(new Produto(null, "Mouse", new BigDecimal("149.90"), "Descrição sensível"));


        //act + assert juntos aqui = porque o MockMvc já permite encadear a chamada com as verificações

        // 2. Simula um DELETE para /v1/produtos/{id}
        mockMvc.perform(delete("/v1/produtos/{id}", produtoSalvo.getId()))
                .andExpect(status().isNoContent());                            // verifica status 204


        //assert extra — confirma diretamente no banco que o produto foi realmente removido

        // 3. Verifica que o produto não existe mais no repository
        assertThat(produtoRepository.existsById(produtoSalvo.getId())).isFalse();
    }
}
