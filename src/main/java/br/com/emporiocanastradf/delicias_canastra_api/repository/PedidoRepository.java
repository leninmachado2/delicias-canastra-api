package br.com.emporiocanastradf.delicias_canastra_api.repository;

import br.com.emporiocanastradf.delicias_canastra_api.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
}
