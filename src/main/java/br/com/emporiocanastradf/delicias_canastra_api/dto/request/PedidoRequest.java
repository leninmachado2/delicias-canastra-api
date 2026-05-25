package br.com.emporiocanastradf.delicias_canastra_api.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoRequest {

    @NotBlank
    private String nome;

    @NotBlank
    private String telefone;

    @NotBlank
    private String pontoRetirada;

    @Valid
    @NotEmpty
    private List<PedidoItemRequest> itens;
}
