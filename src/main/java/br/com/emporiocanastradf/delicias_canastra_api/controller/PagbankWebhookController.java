package br.com.emporiocanastradf.delicias_canastra_api.controller;

import br.com.emporiocanastradf.delicias_canastra_api.model.Pedido;
import br.com.emporiocanastradf.delicias_canastra_api.repository.PedidoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/pagbank/webhook")
public class PagbankWebhookController {

    private static final Logger logger = LoggerFactory.getLogger(PagbankWebhookController.class);
    private final PedidoRepository pedidoRepository;

    public PagbankWebhookController(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    @PostMapping("/checkout")
    @Transactional
    public ResponseEntity<Void> receberCheckoutWebhook(
            @RequestBody Map<String, Object> payload,
            @RequestHeader(value = "X-PagBank-Signature", required = false) String signatureHeader) {

        logger.info("Webhook PagBank recebido: signature={}, payload={}", signatureHeader, payload);

        String referenceId = String.valueOf(payload.getOrDefault("reference_id", ""));
        String checkoutStatus = String.valueOf(payload.getOrDefault("status", ""));

        if (referenceId.startsWith("PEDIDO-")) {
            try {
                long pedidoId = Long.parseLong(referenceId.substring(7));
                pedidoRepository.findById(pedidoId).ifPresent(pedido -> atualizarStatusPagamento(pedido, checkoutStatus));
            } catch (NumberFormatException ex) {
                logger.warn("Reference ID do webhook não está no formato esperado: {}", referenceId);
            }
        } else {
            logger.warn("Webhook PagBank recebido sem reference_id válido: {}", referenceId);
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    private void atualizarStatusPagamento(Pedido pedido, String status) {
        if (status == null) {
            return;
        }

        switch (status.toUpperCase()) {
            case "PAID":
            case "PAGO":
            case "AUTHORIZED":
                pedido.setStatusPagamento("PAGO");
                break;
            case "EXPIRED":
            case "CANCELLED":
            case "CANCELED":
                pedido.setStatusPagamento("CANCELADO");
                break;
            default:
                pedido.setStatusPagamento(status.toUpperCase());
                break;
        }

        pedidoRepository.save(pedido);
        logger.info("Pedido {} atualizado para statusPagamento={}", pedido.getId(), pedido.getStatusPagamento());
    }
}
