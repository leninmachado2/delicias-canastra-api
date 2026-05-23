package br.com.emporiodeliciasdacanastra.delicias_canastra_api.controller;

import br.com.emporiodeliciasdacanastra.delicias_canastra_api.model.Produto;
import br.com.emporiodeliciasdacanastra.delicias_canastra_api.repository.ProdutoRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/produtos")
public class DataLoaderController {

    private final ProdutoRepository produtoRepository;

    public DataLoaderController(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    @GetMapping("/popular")
    public String popularBanco() {
        Produto produto1 = new Produto();
        produto1.setNome("KIT PROVOLETO");
        produto1.setCategoria("Queijos");
        produto1.setPreco(83.90);
        produto1.setQuantidadeEstoque(50);
        produtoRepository.save(produto1);

        Produto produto2 = new Produto();
        produto2.setNome("MINAS PADRAO 600GR");
        produto2.setCategoria("Queijos");
        produto2.setPreco(80.85);
        produto2.setQuantidadeEstoque(30);
        produtoRepository.save(produto2);

        Produto produto3 = new Produto();
        produto3.setNome("PARMESAO CUNHA 600GR");
        produto3.setCategoria("Queijos");
        produto3.setPreco(86.97);
        produto3.setQuantidadeEstoque(40);
        produtoRepository.save(produto3);

        Produto produto4 = new Produto();
        produto4.setNome("SALAMINHO ITALIANO (CX 30)");
        produto4.setCategoria("Embutidos");
        produto4.setPreco(320.80);
        produto4.setQuantidadeEstoque(15);
        produtoRepository.save(produto4);

        return "Banco de dados populado com sucesso!";
    }
}