package br.com.emporiodeliciasdacanastra.delicias_canastra_api.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nomeCliente;
    private String telefoneCliente;
    private String pontoRetirada; // "Sede I" ou "Sede III"
    private Double valorTotal;
    private String statusPagamento; // "PENDENTE", "PAGO"
    private LocalDateTime dataPedido;
}