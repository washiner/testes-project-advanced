📋 COMMIT 1 — CRUD + Testes Unitários (Mockito)
1. Docker Compose

Cria docker-compose.yml na raiz
Sobe Postgres + pgAdmin
docker compose up -d

2. Configuração da aplicação

application.yml com dados de conexão (url, username, password)
ddl-auto: update (gera tabela automaticamente)
show-sql: true (debug)

3. Entity (pacote entity)

@Entity + @Table(name = "...")
@Id + @GeneratedValue(strategy = GenerationType.IDENTITY)
@Column(nullable = false) nos campos obrigatórios
@Data + @NoArgsConstructor + @AllArgsConstructor (Lombok)
Inclui campo sensível que não deve aparecer no response

4. Repository (pacote repository)

Interface que estende JpaRepository<Entity, Long>
@Repository
Não precisa escrever métodos — já vem com save, findById, findAll, deleteById, existsById

5. DTOs (pacote dto) — usando record, não classe

XxxRequest — campos que o cliente envia (sem id)
XxxResponse — campos que a API devolve (com id, sem campos sensíveis)

6. Service (pacote service)

@Service + @RequiredArgsConstructor
Injeta o Repository
Método criar — monta entity, salva, converte pra Response
Método listarTodos — busca todos, converte cada um com .stream().map()
Método buscarPorId — busca, usa .orElseThrow() se não achar
Método atualizar — busca, atualiza campos com setters, salva
Método deletar — verifica existsById, lança exceção ou deleta

7. Controller (pacote controller)

@RestController + @RequestMapping("/v1/...") + @RequiredArgsConstructor
POST → @PostMapping + @RequestBody → 201 Created
GET (lista) → @GetMapping → 200 OK
GET (por id) → @GetMapping("/{id}") + @PathVariable → 200 OK
PUT → @PutMapping("/{id}") + @PathVariable + @RequestBody → 200 OK
DELETE → @DeleteMapping("/{id}") + @PathVariable → 204 No Content

8. Testes Unitários (pacote service, mesma estrutura do main)

@ExtendWith(MockitoExtension.class) no topo da classe
@Mock no Repository
@InjectMocks na Service

Para cada método da Service, segue o padrão AAA:

Arrange — cria os objetos de entrada, configura o mock com when(...).thenReturn(...)
Act — chama o método real da Service
Assert — confere o resultado com assertThat(...) e confirma chamadas com verify(...)

Casos extras a sempre cobrir:

Cenário de sucesso
Cenário de erro (item não encontrado → assertThatThrownBy)
Para métodos void (como deletar) → confirma comportamento via verify, nunca via retorno