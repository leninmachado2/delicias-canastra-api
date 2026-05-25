package br.com.emporiocanastradf.delicias_canastra_api.controller;

import br.com.emporiocanastradf.delicias_canastra_api.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CatalogoController {

    @Autowired
    private ProdutoRepository produtoRepository;

    @GetMapping("/")
    public String exibirCatalogo(Model model) {
        // Busca todos os produtos salvos no banco de dados
        model.addAttribute("produtos", produtoRepository.findAll());
        
        // Retorna o nome do arquivo HTML que vai exibir o catálogo (index.html)
        return "index";
    }
}
