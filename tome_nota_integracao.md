📋 COMMIT 2 — Testes de Integração (MockMvc + @SpringBootTest)
Conceito-chave

Teste de integração sobe o Spring inteiro e testa o fluxo real: Controller → Service → Repository → Banco. Diferente do teste unitário, aqui nada é mockado (exceto o banco real por um banco em memória).
1. Dependências no pom.xml
xml<!-- traz JUnit, Mockito, AssertJ -->
spring-boot-starter-test

<!-- no Spring Boot 4, MockMvc foi separado e precisa ser declarado à parte -->
spring-boot-starter-webmvc-test

<!-- banco em memória, só para os testes -->
h2 (scope test)
⚠️ Nota de versão: no Spring Boot 4, @AutoConfigureMockMvc mudou de pacote para org.springframework.boot.webmvc.test.autoconfigure, e o ObjectMapper do Jackson 3 mudou para tools.jackson.databind.ObjectMapper. Se a versão do Spring mudar de novo, sempre vale checar a documentação oficial.
2. application.yml separado para testes

Local: src/test/resources/application.yml
Usa H2 em memória (jdbc:h2:mem:testdb)
ddl-auto: create-drop — cria a tabela ao iniciar o contexto, destrói ao final

3. Estrutura da classe de teste (mesmo pacote do Controller testado)
@SpringBootTest          → sobe o contexto Spring inteiro
@AutoConfigureMockMvc     → configura o MockMvc

@Autowired MockMvc        → simula requisições HTTP
@Autowired ObjectMapper    → converte objeto Java ↔ JSON
@Autowired Repository      → usado para inserir/verificar dados direto no banco nos testes
4. @BeforeEach — limpar o banco antes de cada teste
java@BeforeEach
void limparBanco() {
    repository.deleteAll();
}
⚠️ Isso é essencial. Sem isso, dados de um teste "sobram" para o próximo, e os testes ficam dependentes de ordem de execução — um dos bugs mais comuns em testes de integração.
5. Padrão de cada teste

POST — monta o objeto, converte para JSON com objectMapper.writeValueAsString(), chama mockMvc.perform(post(...).contentType(...).content(json)), verifica status().isCreated() e os campos com jsonPath("$.campo").value(...)
GET por ID — insere o dado direto via repository.save(), chama mockMvc.perform(get("/rota/{id}", id)), verifica status().isOk() e os campos
GET listar todos — insere vários dados via repository.save(), chama mockMvc.perform(get("/rota")), verifica jsonPath("$.length()").value(quantidade)
PUT — insere o dado original, monta o request com dados novos, converte para JSON, chama mockMvc.perform(put("/rota/{id}", id)...), verifica os campos atualizados
DELETE — insere o dado, chama mockMvc.perform(delete("/rota/{id}", id)), verifica status().isNoContent(), e confirma com repository.existsById(id) que realmente foi removido