package br.com.emporiocanastradf.delicias_canastra_api.service;

import br.com.emporiocanastradf.delicias_canastra_api.dto.request.PedidoItemRequest;
import br.com.emporiocanastradf.delicias_canastra_api.dto.request.PedidoRequest;
import br.com.emporiocanastradf.delicias_canastra_api.dto.response.PedidoResponse;
import br.com.emporiocanastradf.delicias_canastra_api.model.Cliente;
import br.com.emporiocanastradf.delicias_canastra_api.model.Pedido;
import br.com.emporiocanastradf.delicias_canastra_api.model.PedidoItem;
import br.com.emporiocanastradf.delicias_canastra_api.model.Produto;
import br.com.emporiocanastradf.delicias_canastra_api.repository.ClienteRepository;
import br.com.emporiocanastradf.delicias_canastra_api.repository.PedidoRepository;
import br.com.emporiocanastradf.delicias_canastra_api.repository.ProdutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final ProdutoRepository produtoRepository;
    private final PagamentoService pagamentoService;

    public PedidoService(PedidoRepository pedidoRepository,
                         ClienteRepository clienteRepository,
                         ProdutoRepository produtoRepository,
                         PagamentoService pagamentoService) {
        this.pedidoRepository = pedidoRepository;
        this.clienteRepository = clienteRepository;
        this.produtoRepository = produtoRepository;
        this.pagamentoService = pagamentoService;
    }

    @Transactional
    public PedidoResponse criarPedido(PedidoRequest request) {
        validarPedido(request);

        Cliente cliente = buscarOuCriarCliente(request.getNome(), request.getTelefone(), request.getPontoRetirada());
        cliente.setPontoRetiradaPreferido(request.getPontoRetirada());
        clienteRepository.save(cliente);

        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setPontoRetirada(request.getPontoRetirada());
        pedido.setDataPedido(LocalDateTime.now());
        pedido.setStatusPagamento("AGUARDANDO_PAGAMENTO");
        pedido.setStatusEntrega("RESERVADO");

        double valorTotal = 0.0;
        for (PedidoItemRequest itemRequest : request.getItens()) {
            Produto produto = produtoRepository.findById(itemRequest.getProdutoId())
                    .orElseThrow(() -> new ResponseStatusException(BAD_REQUEST, "Produto não encontrado: " + itemRequest.getProdutoId()));

            if (itemRequest.getQuantidade() > produto.getQuantidadeEstoque()) {
                throw new ResponseStatusException(BAD_REQUEST, "Estoque insuficiente para produto: " + produto.getNome());
            }

            produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() - itemRequest.getQuantidade());
            produtoRepository.save(produto);

            double subtotal = produto.getPreco() * itemRequest.getQuantidade();
            PedidoItem pedidoItem = new PedidoItem();
            pedidoItem.setProdutoId(produto.getId());
            pedidoItem.setNomeProduto(produto.getNome());
            pedidoItem.setPrecoUnitario(produto.getPreco());
            pedidoItem.setQuantidade(itemRequest.getQuantidade());
            pedidoItem.setSubtotal(subtotal);
            pedidoItem.setPedido(pedido);

            pedido.getItens().add(pedidoItem);
            valorTotal += subtotal;
        }

        pedido.setValorTotal(valorTotal);
        pedido = pedidoRepository.save(pedido);

        var pagamento = pagamentoService.criarPagamento(pedido);
        pedido.setTransacaoPagBankId(pagamento.getTransactionId());
        pedido = pedidoRepository.save(pedido);

        return new PedidoResponse(
                pedido.getId(),
                pedido.getValorTotal(),
                pedido.getStatusPagamento(),
                pedido.getStatusEntrega(),
                pagamento.getPaymentUrl(),
                "Pedido criado com sucesso. Use o link de pagamento para concluir." );
    }

    private void validarPedido(PedidoRequest request) {
        if (request.getItens() == null || request.getItens().isEmpty()) {
            throw new ResponseStatusException(BAD_REQUEST, "Carrinho vazio.");
        }
        if (request.getNome() == null || request.getNome().isBlank()) {
            throw new ResponseStatusException(BAD_REQUEST, "Nome do cliente é obrigatório.");
        }
        if (request.getTelefone() == null || request.getTelefone().isBlank()) {
            throw new ResponseStatusException(BAD_REQUEST, "Telefone do cliente é obrigatório.");
        }
        if (request.getPontoRetirada() == null || request.getPontoRetirada().isBlank()) {
            throw new ResponseStatusException(BAD_REQUEST, "Ponto de retirada é obrigatório.");
        }
    }

    private Cliente buscarOuCriarCliente(String nome, String telefone, String pontoRetirada) {
        Optional<Cliente> clienteOpt = clienteRepository.findByTelefoneWhatsApp(telefone);
        if (clienteOpt.isPresent()) {
            Cliente cliente = clienteOpt.get();
            cliente.setNome(nome);
            cliente.setPontoRetiradaPreferido(pontoRetirada);
            return cliente;
        }

        Cliente cliente = new Cliente();
        cliente.setNome(nome);
        cliente.setTelefoneWhatsApp(telefone);
        cliente.setCodigoCliente(gerarCodigoCliente());
        cliente.setDataCadastro(LocalDateTime.now());
        cliente.setPontoRetiradaPreferido(pontoRetirada);
        return cliente;
    }

    private String gerarCodigoCliente() {
        int next = clienteRepository.findMaxCodigoNumerico().orElse(0) + 1;
        return String.format("CDC-%05d", next);
    }
}
