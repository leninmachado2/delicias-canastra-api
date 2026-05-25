package br.com.emporiocanastradf.delicias_canastra_api.repository;

import br.com.emporiocanastradf.delicias_canastra_api.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
}
