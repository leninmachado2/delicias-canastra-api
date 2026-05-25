package br.com.emporiocanastradf.delicias_canastra_api.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/produtos")
@Deprecated(since = "2.0", forRemoval = true)
public class DataLoaderController {
    // Rota descontinuada: use DataLoaderRunner para carregamento automático via CSV
}
