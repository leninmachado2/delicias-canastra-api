package br.com.emporiocanastradf.delicias_canastra_api.service;

import br.com.emporiocanastradf.delicias_canastra_api.model.Pedido;

public interface PagamentoService {

    PagbankPaymentResult criarPagamento(Pedido pedido);
}

