package br.com.emporiocanastradf.delicias_canastra_api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa um pedido feito pelo catálogo online.
 *
 * Regras de negócio:
 *  - Todo pedido está vinculado a um Cliente identificado.
 *  - O pagamento é obrigatório via PagBank antes da confirmação.
 *  - O status evolui: AGUARDANDO_PAGAMENTO → RESERVADO → AGENDADO → ENTREGUE
 *  - Dois não-comparecimentos disparam alerta de qualidade pelo bot.
 *
 * Relacionamento:
 *  - @ManyToOne com Cliente: muitos pedidos para um cliente.
 *  - O campo cliente_id é a chave estrangeira na tabela pedidos.
 */
@Entity
@Table(name = "pedidos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // -------------------------------------------------------
    // Relacionamento com o Cliente
    // -------------------------------------------------------

    /**
     * Cliente que fez o pedido.
     * FetchType.LAZY: o cliente só é carregado do banco quando acessado,
     * evitando consultas desnecessárias ao listar pedidos.
     *
     * @JoinColumn cria a coluna cliente_id na tabela pedidos.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    // -------------------------------------------------------
    // Dados do pedido
    // -------------------------------------------------------

    /** Ponto de retirada escolhido: "BB Sede I" ou "BB Sede III". */
    @Column(nullable = false, length = 20)
    private String pontoRetirada;

    /** Valor total do pedido calculado no momento da finalização. */
    @Column(nullable = false)
    private Double valorTotal;

    /** Data e hora em que o pedido foi criado no sistema. */
    @Column(nullable = false)
    private LocalDateTime dataPedido;

    /**
     * Data agendada para retirada, escolhida pelo cliente via bot.
     * Preenchida após o bot oferecer as 3 próximas datas disponíveis.
     */
    private LocalDateTime dataAgendada;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<PedidoItem> itens = new ArrayList<>();

    // -------------------------------------------------------
    // Controle de status
    // -------------------------------------------------------

    /**
     * Status do pagamento:
     *  AGUARDANDO_PAGAMENTO — pedido criado, aguardando confirmação PagBank
     *  PAGO                 — pagamento confirmado, produtos reservados
     *  CANCELADO            — pagamento não realizado ou pedido cancelado
     */
    @Column(nullable = false, length = 25)
    private String statusPagamento = "AGUARDANDO_PAGAMENTO";

    /**
     * Status da entrega:
     *  RESERVADO    — pagamento confirmado, aguardando agendamento
     *  AGENDADO     — cliente escolheu o dia de retirada via bot
     *  DISPONIVEL   — Allan confirmou presença no ponto naquele dia
     *  ENTREGUE     — cliente ou Allan confirmaram a retirada
     *  NAO_RETIRADO — cliente não compareceu (incrementa contadorFaltas)
     */
    @Column(nullable = false, length = 20)
    private String statusEntrega = "RESERVADO";

    /**
     * Quantidade de vezes que o cliente agendou mas não compareceu.
     * Quando chega a 2, o bot dispara alerta de qualidade sobre
     * conservação do queijo e riscos de não refrigeração.
     */
    @Column(nullable = false)
    private Integer contadorFaltas = 0;

    /**
     * ID da transação retornado pelo PagBank após pagamento aprovado.
     * Usado para conciliação financeira e comprovante ao cliente.
     */
    @Column(length = 100)
    private String transacaoPagBankId;

    // -------------------------------------------------------
    // Método utilitário
    // -------------------------------------------------------

    /**
     * Registra uma falta do cliente.
     * Incrementa o contador e retorna true se atingiu 2 faltas,
     * sinalizando ao serviço que o alerta de qualidade deve ser disparado.
     */
    public boolean registrarFalta() {
        this.contadorFaltas++;
        this.statusEntrega = "NAO_RETIRADO";
        return this.contadorFaltas >= 2;
    }
}
