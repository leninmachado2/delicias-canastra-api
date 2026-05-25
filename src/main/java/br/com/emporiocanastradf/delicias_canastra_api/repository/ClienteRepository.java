package br.com.emporiocanastradf.delicias_canastra_api.repository;

import br.com.emporiocanastradf.delicias_canastra_api.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Repositório JPA para a entidade Cliente.
 *
 * Além dos métodos padrão do JpaRepository (findAll, findById, save, delete...),
 * declara queries específicas para os fluxos do bot e do catálogo.
 */
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    /**
     * Busca um cliente pelo número do WhatsApp.
     * Usado pelo webhook da Evolution API para identificar
     * quem enviou a mensagem antes de responder.
     *
     * Exemplo de uso:
     *   Optional<Cliente> cliente = clienteRepository
     *       .findByTelefoneWhatsApp("5561999990000");
     */
    Optional<Cliente> findByTelefoneWhatsApp(String telefoneWhatsApp);

    /**
     * Busca um cliente pelo código amigável CDC-00001.
     * Usado quando o cliente novo acessa o catálogo via link genérico
     * com parâmetro ?cliente=CDC-00001.
     */
    Optional<Cliente> findByCodigoCliente(String codigoCliente);

    /**
     * Busca um cliente pelo token de acesso e valida se ainda não expirou.
     * Usado pelo catálogo ao receber ?token=abc123 na URL.
     *
     * A query verifica simultaneamente:
     *  - se o token existe e corresponde ao cliente
     *  - se o token ainda está dentro do prazo de validade
     *
     * Exemplo de uso:
     *   Optional<Cliente> cliente = clienteRepository
     *       .findByTokenValido("abc123", LocalDateTime.now());
     */
    @Query("SELECT c FROM Cliente c " +
           "WHERE c.tokenAcesso = :token " +
           "AND c.tokenExpiracao > :agora")
    Optional<Cliente> findByTokenValido(
            @Param("token") String token,
            @Param("agora") LocalDateTime agora);

    /**
     * Verifica se já existe um cliente cadastrado com aquele número.
     * Usado antes de criar um novo cadastro para evitar duplicatas.
     */
    boolean existsByTelefoneWhatsApp(String telefoneWhatsApp);

    /**
     * Retorna o maior código CDC numérico cadastrado.
     * Usado para gerar o próximo código em sequência (CDC-00001, CDC-00002...).
     *
     * A query extrai a parte numérica do codigoCliente com SUBSTRING e
     * converte para inteiro para garantir ordenação correta.
     */
    @Query("SELECT MAX(CAST(SUBSTRING(c.codigoCliente, 5) AS int)) FROM Cliente c")
    Optional<Integer> findMaxCodigoNumerico();
}
