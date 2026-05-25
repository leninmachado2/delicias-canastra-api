package br.com.emporiocanastradf.delicias_canastra_api.controller;

import br.com.emporiocanastradf.delicias_canastra_api.model.Produto;
import br.com.emporiocanastradf.delicias_canastra_api.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/admin/produtos")
public class AdminProdutoController {

    @Autowired
    private ProdutoRepository produtoRepository;

    @GetMapping
    public String listarProdutos(Model model) {
        model.addAttribute("produtos", produtoRepository.findAll());
        model.addAttribute("novoProduto", new Produto());
        return "admin/produtos";
    }

    @GetMapping("/editar/{id}")
    public String editarProduto(@PathVariable Long id, Model model) {
        Optional<Produto> produtoOpt = produtoRepository.findById(id);
        Produto produto = produtoOpt.orElse(new Produto());
        model.addAttribute("produtos", produtoRepository.findAll());
        model.addAttribute("novoProduto", produto);
        return "admin/produtos";
    }

    @PostMapping("/salvar")
    public String salvarProduto(@ModelAttribute Produto produto) {
        produtoRepository.save(produto);
        return "redirect:/admin/produtos";
    }

    @GetMapping("/excluir/{id}")
    public String excluirProduto(@PathVariable Long id) {
        produtoRepository.deleteById(id);
        return "redirect:/admin/produtos";
    }
}
