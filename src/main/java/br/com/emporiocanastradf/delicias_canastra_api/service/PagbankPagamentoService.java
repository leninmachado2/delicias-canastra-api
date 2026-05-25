package br.com.emporiocanastradf.delicias_canastra_api.service;

import br.com.emporiocanastradf.delicias_canastra_api.model.Pedido;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class PagbankPagamentoService implements PagamentoService {

    private final RestTemplate restTemplate;
    private final String apiUrl;
    private final String apiKey;
    private final boolean sandbox;
    private final String redirectUrl;
    private final String returnUrl;
    private final String notificationUrl;

    public PagbankPagamentoService(
            @Value("${pagbank.api.url:https://sandbox.api.pagseguro.com}") String apiUrl,
            @Value("${pagbank.api.key:}") String apiKey,
            @Value("${pagbank.api.sandbox:true}") boolean sandbox,
            @Value("${pagbank.redirect-url:http://localhost:8082}") String redirectUrl,
            @Value("${pagbank.return-url:http://localhost:8082}") String returnUrl,
            @Value("${pagbank.notification-url:http://localhost:8082/api/pagbank/webhook/checkout}") String notificationUrl) {
        this.restTemplate = new RestTemplate();
        this.apiUrl = apiUrl;
        this.apiKey = apiKey;
        this.sandbox = sandbox;
        this.redirectUrl = redirectUrl;
        this.returnUrl = returnUrl;
        this.notificationUrl = notificationUrl;
    }

    @Override
    public PagbankPaymentResult criarPagamento(Pedido pedido) {
        if (apiKey == null || apiKey.isBlank()) {
            return criarPagamentoSimulado(pedido);
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            Map<String, Object> body = new HashMap<>();
            body.put("reference_id", "PEDIDO-" + pedido.getId());
            body.put("customer_modifiable", true);
            body.put("redirect_url", redirectUrl);
            body.put("return_url", returnUrl);
            body.put("notification_urls", Collections.singletonList(notificationUrl));
            body.put("payment_notification_urls", Collections.singletonList(notificationUrl));
            body.put("sandbox", sandbox);

            Map<String, Object> customer = new HashMap<>();
            customer.put("name", pedido.getCliente().getNome());
            Map<String, Object> phone = parsePhone(pedido.getCliente().getTelefoneWhatsApp());
            if (!phone.isEmpty()) {
                customer.put("phone", phone);
            }
            body.put("customer", customer);

            body.put("items", buildItems(pedido));

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            Map<String, Object> response = restTemplate.postForObject(apiUrl + "/checkouts", request, Map.class);

            if (response == null) {
                return criarPagamentoSimulado(pedido);
            }

            String checkoutId = response.getOrDefault("id", "").toString();
            String paymentUrl = extractPaymentLink(response);
            if (paymentUrl.isBlank()) {
                paymentUrl = gerarLinkPagamento(pedido);
            }

            return new PagbankPaymentResult(checkoutId, paymentUrl);
        } catch (RestClientException ex) {
            return criarPagamentoSimulado(pedido);
        }
    }

    public Map<String, Object> consultarCheckout(String checkoutId) {
        if (apiKey == null || apiKey.isBlank()) {
            return Collections.emptyMap();
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(apiKey);
            HttpEntity<Void> request = new HttpEntity<>(headers);
            return restTemplate.exchange(apiUrl + "/checkouts/" + checkoutId, HttpMethod.GET, request, Map.class).getBody();
        } catch (RestClientException ex) {
            return Collections.emptyMap();
        }
    }

    private List<Map<String, Object>> buildItems(Pedido pedido) {
        List<Map<String, Object>> items = new ArrayList<>();
        pedido.getItens().forEach(item -> {
            Map<String, Object> itemData = new HashMap<>();
            itemData.put("reference_id", "PRODUTO-" + item.getProdutoId());
            itemData.put("name", item.getNomeProduto());
            itemData.put("quantity", item.getQuantidade());
            itemData.put("unit_amount", Math.round(item.getPrecoUnitario() * 100));
            items.add(itemData);
        });
        return items;
    }

    private String extractPaymentLink(Map<String, Object> response) {
        Object linksObject = response.get("links");
        if (linksObject instanceof List) {
            for (Object linkObject : (List<?>) linksObject) {
                if (linkObject instanceof Map) {
                    Map<?, ?> link = (Map<?, ?>) linkObject;
                    if ("PAY".equals(link.get("rel"))) {
                        return String.valueOf(link.get("href"));
                    }
                }
            }
        }
        return "";
    }

    private Map<String, Object> parsePhone(String telefone) {
        if (telefone == null) {
            return Collections.emptyMap();
        }

        String digits = telefone.replaceAll("\\D", "");
        if (digits.length() < 10) {
            return Collections.emptyMap();
        }

        String country = "+55";
        String area;
        String number;

        if (digits.length() > 10) {
            country = "+" + digits.substring(0, digits.length() - 10);
            area = digits.substring(digits.length() - 10, digits.length() - 8);
        } else {
            area = digits.substring(0, 2);
        }
        number = digits.substring(digits.length() - 8);

        Map<String, Object> phone = new HashMap<>();
        phone.put("country", country);
        phone.put("area", area);
        phone.put("number", number);
        return phone;
    }

    private PagbankPaymentResult criarPagamentoSimulado(Pedido pedido) {
        String transactionId = "SIMULADO-" + UUID.randomUUID();
        return new PagbankPaymentResult(transactionId, gerarLinkPagamento(pedido));
    }

    private String gerarLinkPagamento(Pedido pedido) {
        return "https://sandbox.pagbank.com/pagamento/" + pedido.getId();
    }
}
