package br.com.emporiocanastradf.delicias_canastra_api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoResponse {

    private Long pedidoId;
    private Double valorTotal;
    private String statusPagamento;
    private String statusEntrega;
    private String paymentUrl;
    private String mensagem;
}
