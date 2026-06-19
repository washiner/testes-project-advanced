package com.washiner.testes.service;

import com.washiner.testes.dto.ProdutoRequest;
import com.washiner.testes.dto.ProdutoResponse;
import com.washiner.testes.entity.Produto;
import com.washiner.testes.repository.ProdutoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // ativa o Mockito nessa classe de teste
public class ProdutoServiceTest {

    @Mock                           // cria um repository falso (fake)
    private ProdutoRepository produtoRepository;

    @InjectMocks                    // injeta o mock dentro da service de verdade
    private ProdutoService produtoService;

    @BeforeEach                     // roda antes de cada teste
    void setup(){
    }

    @Test
    void deveCriarProdutoComSucesso(){
        //O método criar recebe um ProdutoRequest e devolve um ProdutoResponse.
        // Internamente ele chama repository.save()

        //Passo a passo:
        //
        //Crie um ProdutoRequest — é o que o "cliente" mandaria. Pense: nome, preço, descrição.
        //Crie um Produto (entity) já com ID — simulando o que o banco devolveria depois de salvar. Por que com ID? Porque no banco de verdade, ao salvar, ele gera o ID automaticamente.
        //Diga ao mock o que fazer — usando when(...).thenReturn(...): "quando chamarem repository.save() com qualquer Produto, devolva esse Produto com ID que eu criei no passo 2".

        //arrange - prepara o cenario


        // 1. Cria o request simulando o que o cliente enviaria no body da requisição
        ProdutoRequest request = new ProdutoRequest("Teclado", new BigDecimal("299.90"), "Descrição sensível");
        // 2. Cria o produto já "salvo", com ID, simulando o retorno do banco
        Produto produtoSalvo = new Produto(1L, "Teclado", new BigDecimal("299.90"), "Descrição sensível");

        // 3. Configura o mock: quando o repository.save() for chamado com qualquer Produto, devolve o produtoSalvo
        when(produtoRepository.save(any(Produto.class))).thenReturn(produtoSalvo);


        //act = a parte onde você realmente executa o método que está sendo testado.
        //Pensa: "Eu já preparei o cenário (request, produto salvo, mock configurado).
        // Agora eu chamo o método de verdade da service e guardo o resultado."

        // 4. Executa o método criar() da service de verdade, passando o request preparado
        ProdutoResponse response = produtoService.criar(request);

        //ASSERT — a parte onde você confirma que o resultado é o que você esperava.
        //Pensa: "O que eu preciso confirmar sobre o response que recebi?"
        //Três coisas importantes: o ID, o nome e o preço.

        // 5. Verifica se o id do response é o mesmo que veio do produtoSalvo
        assertThat(response.id()).isEqualTo(1L);

        // 6. Verifica se o nome do response está correto
        assertThat(response.nome()).isEqualTo("Teclado");

        // 7. Verifica se o preço do response está correto
        assertThat(response.preco()).isEqualTo(new BigDecimal("299.90"));

        // 8. Verifica que o método save() do repository foi chamado exatamente 1 vez
        verify(produtoRepository, times(1)).save(any(Produto.class));
    }

    @Test
    void deveListarTodosOsProdutos(){
        //O método listarTodos não recebe parâmetros e devolve uma List<ProdutoResponse>.
        // Internamente ele chama repository.findAll() e converte cada Produto em ProdutoResponse

        //Passo a passo:
        //
        //Crie uma lista de Produtos (entity) — simulando os registros que existem no banco.
        //Diga ao mock o que fazer — "quando chamarem findAll(), devolva essa lista de produtos".

        //arrange - prepara o cenario

        // 1. Cria o primeiro produto simulando um registro existente no banco
        Produto produto1 = new Produto(1L, "Teclado", new BigDecimal("299.90"), "Descrição sensível 1");

        // 2. Cria o segundo produto simulando outro registro existente no banco
        Produto produto2 = new Produto(2L, "Mouse", new BigDecimal("149.90"), "Descrição sensível 2");

        // 3. Configura o mock: quando findAll() for chamado, devolve a lista com os dois produtos
        when(produtoRepository.findAll()).thenReturn(List.of(produto1, produto2));


        //act = a parte onde você realmente executa o método que está sendo testado.
        //Pensa: "Eu já preparei o cenário (lista de produtos, mock configurado).
        // Agora eu chamo o método de verdade da service e guardo o resultado."

        // 4. Executa o método listarTodos() da service de verdade
        List<ProdutoResponse> response = produtoService.listarTodos();


        //ASSERT — a parte onde você confirma que o resultado é o que você esperava.
        //Pensa: "O que eu preciso confirmar sobre a lista que recebi?"
        //O tamanho da lista e se os dados convertidos estão corretos.

        // 5. Verifica se a lista devolvida tem exatamente 2 produtos
        assertThat(response).hasSize(2);

        // 6. Verifica se o nome do primeiro produto da lista está correto
        assertThat(response.get(0).nome()).isEqualTo("Teclado");

        // 7. Verifica se o nome do segundo produto da lista está correto
        assertThat(response.get(1).nome()).isEqualTo("Mouse");

        // 8. Verifica que o método findAll() do repository foi chamado exatamente 1 vez
        verify(produtoRepository, times(1)).findAll();
    }

    @Test
    void deveBuscarProdutoPorIdComSucesso(){
        //O método buscarPorId recebe um Long (id) e devolve um ProdutoResponse.
        // Internamente ele chama repository.findById()

        //Passo a passo:
        //
        //Crie um Produto (entity) já com ID — simulando um produto que já existe no banco.
        //Diga ao mock o que fazer — usando when(...).thenReturn(...): "quando chamarem repository.findById() com esse id, devolva um Optional contendo esse Produto".

        //arrange - prepara o cenario

        // 1. Cria o produto simulando um registro que já existe no banco
        Produto produtoExistente = new Produto(1L, "Teclado", new BigDecimal("299.90"), "Descrição sensível");

        // 2. Configura o mock: quando o repository.findById(1L) for chamado, devolve um Optional com o produto
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produtoExistente));


        //act = a parte onde você realmente executa o método que está sendo testado.
        //Pensa: "Eu já preparei o cenário (produto existente, mock configurado).
        // Agora eu chamo o método de verdade da service e guardo o resultado."

        // 3. Executa o método buscarPorId() da service de verdade, passando o id
        ProdutoResponse response = produtoService.buscarPorId(1L);

        //ASSERT — a parte onde você confirma que o resultado é o que você esperava.
        //Pensa: "O que eu preciso confirmar sobre o response que recebi?"
        //Três coisas importantes: o ID, o nome e o preço.

        // 4. Verifica se o id do response é o mesmo do produto existente
        assertThat(response.id()).isEqualTo(1L);

        // 5. Verifica se o nome do response está correto
        assertThat(response.nome()).isEqualTo("Teclado");

        // 6. Verifica se o preço do response está correto
        assertThat(response.preco()).isEqualTo(new BigDecimal("299.90"));

        // 7. Verifica que o método findById() do repository foi chamado exatamente 1 vez
        verify(produtoRepository, times(1)).findById(1L);
    }

    @Test
    void deveLancarExcecaoQuandoProdutoNaoExiste(){
        //O método buscarPorId, quando não encontra o produto, lança uma RuntimeException.
        // Internamente ele chama repository.findById() e usa orElseThrow().

        //Passo a passo:
        //
        //Diga ao mock o que fazer — usando when(...).thenReturn(...): "quando chamarem repository.findById() com esse id, devolva um Optional vazio (simulando que não existe)".

        //arrange - prepara o cenario

        // 1. Configura o mock: quando o repository.findById(99L) for chamado, devolve um Optional vazio
        when(produtoRepository.findById(99L)).thenReturn(Optional.empty());


        //act + assert juntos aqui = porque estamos testando uma exceção, não um valor de retorno
        //Pensa: "Eu preciso confirmar que, ao chamar o método com um id inexistente, ele lança a exceção certa."

        // 2. Executa o método e verifica que ele lança RuntimeException com a mensagem esperada
        assertThatThrownBy(() -> produtoService.buscarPorId(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Produto não encontrado");

        // 3. Verifica que o método findById() do repository foi chamado exatamente 1 vez
        verify(produtoRepository, times(1)).findById(99L);
    }

    @Test
    void deveAtualizarProdutoComSucesso(){
        //O método atualizar recebe um id e um ProdutoRequest, e devolve um ProdutoResponse.
        // Internamente ele busca o produto com findById(), atualiza os campos e chama save()

        //Passo a passo:
        //
        //Crie um ProdutoRequest com os dados novos — é o que o "cliente" mandaria pra atualizar.
        //Crie um Produto "existente" — simulando o que já está salvo no banco antes da atualização.
        //Crie um Produto "atualizado" — simulando o que o banco devolveria depois do save() com os dados novos.
        //Diga ao mock o que fazer no findById — "quando chamarem findById(id), devolva o produto existente".
        //Diga ao mock o que fazer no save — "quando chamarem save() com qualquer Produto, devolva o produto atualizado".

        //arrange - prepara o cenario

        // 1. Cria o request com os novos dados que o cliente quer atualizar
        ProdutoRequest request = new ProdutoRequest("Teclado Gamer", new BigDecimal("349.90"), "Nova descrição sensível");

        // 2. Cria o produto como ele está atualmente no banco, antes da atualização
        Produto produtoExistente = new Produto(1L, "Teclado", new BigDecimal("299.90"), "Descrição sensível");

        // 3. Cria o produto como ele ficaria depois de atualizado e salvo
        Produto produtoAtualizado = new Produto(1L, "Teclado Gamer", new BigDecimal("349.90"), "Nova descrição sensível");

        // 4. Configura o mock: quando findById(1L) for chamado, devolve o produto existente
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produtoExistente));

        // 5. Configura o mock: quando save() for chamado com qualquer Produto, devolve o produto atualizado
        when(produtoRepository.save(any(Produto.class))).thenReturn(produtoAtualizado);


        //act = a parte onde você realmente executa o método que está sendo testado.
        //Pensa: "Eu já preparei o cenário (request, produto existente, produto atualizado, mocks configurados).
        // Agora eu chamo o método de verdade da service e guardo o resultado."

        // 6. Executa o método atualizar() da service, passando o id e o request
        ProdutoResponse response = produtoService.atualizar(1L, request);

        //ASSERT — a parte onde você confirma que o resultado é o que você esperava.
        //Pensa: "O que eu preciso confirmar sobre o response que recebi?"
        //Que os dados novos foram aplicados corretamente.

        // 7. Verifica se o nome do response foi atualizado corretamente
        assertThat(response.nome()).isEqualTo("Teclado Gamer");

        // 8. Verifica se o preço do response foi atualizado corretamente
        assertThat(response.preco()).isEqualTo(new BigDecimal("349.90"));

        // 9. Verifica que o método findById() do repository foi chamado exatamente 1 vez
        verify(produtoRepository, times(1)).findById(1L);

        // 10. Verifica que o método save() do repository foi chamado exatamente 1 vez
        verify(produtoRepository, times(1)).save(any(Produto.class));
    }

    @Test
    void deveDeletarProdutoComSucesso(){
        //O método deletar recebe um id e não devolve nada (void).
        // Internamente ele verifica existsById() e, se existir, chama deleteById()

        //Passo a passo:
        //
        //Diga ao mock o que fazer no existsById — "quando chamarem existsById(id), devolva true (produto existe)".
        //Não precisa configurar o deleteById() porque ele é void — métodos void não retornam nada pro when().

        //arrange - prepara o cenario

        // 1. Configura o mock: quando existsById(1L) for chamado, devolve true (produto existe)
        when(produtoRepository.existsById(1L)).thenReturn(true);


        //act = a parte onde você realmente executa o método que está sendo testado.
        //Pensa: "Eu já preparei o cenário (mock configurado dizendo que o produto existe).
        // Agora eu chamo o método de verdade da service."

        // 2. Executa o método deletar() da service, passando o id
        produtoService.deletar(1L);

        //ASSERT — a parte onde você confirma que o comportamento esperado aconteceu.
        //Pensa: "Como eu confirmo que algo foi deletado, já que o método não devolve nada?"
        //Eu verifico se os métodos certos do repository foram chamados.

        // 3. Verifica que o método existsById() do repository foi chamado exatamente 1 vez
        verify(produtoRepository, times(1)).existsById(1L);

        // 4. Verifica que o método deleteById() do repository foi chamado exatamente 1 vez
        verify(produtoRepository, times(1)).deleteById(1L);
    }

    @Test
    void deveLancarExcecaoAoDeletarProdutoInexistente(){
        //O método deletar, quando o produto não existe, lança uma RuntimeException
        // e nunca chega a chamar o deleteById()

        //Passo a passo:
        //
        //Diga ao mock o que fazer no existsById — "quando chamarem existsById(id), devolva false (produto não existe)".

        //arrange - prepara o cenario

        // 1. Configura o mock: quando existsById(99L) for chamado, devolve false (produto não existe)
        when(produtoRepository.existsById(99L)).thenReturn(false);


        //act + assert juntos aqui = porque estamos testando uma exceção, não um valor de retorno
        //Pensa: "Eu preciso confirmar que, ao tentar deletar um id inexistente, ele lança a exceção certa."

        // 2. Executa o método e verifica que ele lança RuntimeException com a mensagem esperada
        assertThatThrownBy(() -> produtoService.deletar(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Produto não encontrado");

        // 3. Verifica que o método existsById() do repository foi chamado exatamente 1 vez
        verify(produtoRepository, times(1)).existsById(99L);

        // 4. Verifica que o método deleteById() NUNCA foi chamado, já que o produto não existe
        verify(produtoRepository, never()).deleteById(anyLong());
    }
}
