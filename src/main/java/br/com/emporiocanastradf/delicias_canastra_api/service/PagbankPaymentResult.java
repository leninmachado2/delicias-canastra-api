package br.com.emporiocanastradf.delicias_canastra_api.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagbankPaymentResult {

    private String transactionId;
    private String paymentUrl;
}
