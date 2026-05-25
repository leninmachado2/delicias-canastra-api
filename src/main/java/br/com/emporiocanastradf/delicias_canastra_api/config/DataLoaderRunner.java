package br.com.emporiocanastradf.delicias_canastra_api.config;

import br.com.emporiocanastradf.delicias_canastra_api.model.Produto;
import br.com.emporiocanastradf.delicias_canastra_api.repository.ProdutoRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

@Configuration
public class DataLoaderRunner {

    @Bean
    public ApplicationRunner loadProductsFromCsv(ProdutoRepository produtoRepository) {
        return args -> {
            if (produtoRepository.count() > 0) {
                System.out.println("Banco de dados já populado, pulando carregamento.");
                return;
            }

            String csvPath = Paths.get("").toAbsolutePath() + "/Arquivos/produtos.csv";
            System.out.println("Carregando produtos de: " + csvPath);

            try (BufferedReader br = new BufferedReader(new FileReader(csvPath, StandardCharsets.UTF_8))) {
                String linha;
                boolean primeiraLinha = true;

                while ((linha = br.readLine()) != null) {
                    if (primeiraLinha) {
                        primeiraLinha = false;
                        continue;
                    }

                    String[] campos = linha.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                    if (campos.length < 6) continue;

                    Produto produto = new Produto();
                    produto.setNome(campos[0].trim());
                    produto.setCategoria(campos[1].trim());
                    produto.setPreco(Double.parseDouble(campos[2].trim()));
                    produto.setQuantidadeEstoque(Integer.parseInt(campos[3].trim()));
                    produto.setDescricao(campos[4].trim());
                    produto.setCaminhoImagem(campos[5].trim());

                    produtoRepository.save(produto);
                    System.out.println("Produto carregado: " + produto.getNome());
                }
                System.out.println("Todos os produtos foram carregados com sucesso!");
            } catch (Exception e) {
                System.err.println("Erro ao carregar produtos do CSV: " + e.getMessage());
                e.printStackTrace();
            }
        };
    }
}
