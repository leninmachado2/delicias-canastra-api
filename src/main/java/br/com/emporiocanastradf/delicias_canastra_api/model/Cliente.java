package br.com.emporiocanastradf.delicias_canastra_api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa um cliente do Empório Delícias da Canastra.
 *
 * Identificação:
 *  - codigoCliente: código amigável e persistente no formato CDC-00001
 *  - telefoneWhatsApp: número usado para identificar o cliente via bot
 *
 * Autenticação sem senha (token mágico):
 *  - tokenAcesso: UUID temporário gerado pelo bot ao identificar o cliente
 *  - tokenExpiracao: data/hora em que o token deixa de ser válido
 *
 * Um cliente pode ter vários pedidos (relacionamento 1:N).
 */
@Entity
@Table(name = "clientes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // -------------------------------------------------------
    // Identificação
    // -------------------------------------------------------

    /** Código amigável e único no formato CDC-00001. Gerado pelo sistema. */
    @Column(unique = true, nullable = false, length = 12)
    private String codigoCliente;

    /** Nome completo ou apelido fornecido pelo cliente no primeiro acesso. */
    @Column(nullable = false)
    private String nome;

    /**
     * Número do WhatsApp no formato internacional sem símbolos.
     * Exemplo: 5561999990000
     * Usado pelo bot para identificar o cliente automaticamente.
     */
    @Column(unique = true, nullable = false, length = 20)
    private String telefoneWhatsApp;

    /** Data e hora do primeiro contato / cadastro. */
    @Column(nullable = false)
    private LocalDateTime dataCadastro;

    // -------------------------------------------------------
    // Token mágico para acesso ao catálogo sem senha
    // -------------------------------------------------------

    /**
     * Token UUID gerado a cada novo acesso via WhatsApp.
     * Enviado como parâmetro no link do catálogo: /catalogo?token=abc123
     * Válido por 15 minutos. Invalidado após uso ou expiração.
     */
    @Column(length = 36)
    private String tokenAcesso;

    /** Momento em que o tokenAcesso expira. Nulo quando não há token ativo. */
    private LocalDateTime tokenExpiracao;

    // -------------------------------------------------------
    // Preferências e histórico
    // -------------------------------------------------------

    /**
     * Ponto de retirada preferido do cliente: "BB Sede I" ou "BB Sede III".
     * Pré-selecionado automaticamente no carrinho nas próximas compras.
     */
    @Column(length = 20)
    private String pontoRetiradaPreferido;

    /**
     * Histórico de pedidos do cliente.
     * mappedBy aponta para o campo "cliente" dentro de Pedido.
     * CascadeType.ALL: ao salvar/deletar o cliente, os pedidos seguem.
     * orphanRemoval: pedidos sem cliente são removidos automaticamente.
     */
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Pedido> pedidos = new ArrayList<>();

    // -------------------------------------------------------
    // Métodos utilitários
    // -------------------------------------------------------

    /**
     * Verifica se o token de acesso ainda é válido.
     * Retorna false se o token for nulo ou já tiver expirado.
     */
    public boolean tokenValido() {
        return tokenAcesso != null
                && tokenExpiracao != null
                && LocalDateTime.now().isBefore(tokenExpiracao);
    }

    /**
     * Invalida o token atual, forçando nova geração no próximo acesso.
     * Chamado após o cliente abrir o catálogo com o link recebido.
     */
    public void invalidarToken() {
        this.tokenAcesso = null;
        this.tokenExpiracao = null;
    }
}
