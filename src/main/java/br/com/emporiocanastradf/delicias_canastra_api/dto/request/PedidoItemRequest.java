package br.com.emporiocanastradf.delicias_canastra_api.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoItemRequest {

    @NotNull
    private Long produtoId;

    @NotNull
    @Min(1)
    private Integer quantidade;
}
