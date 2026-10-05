package com.dimdim.api;

import com.dimdim.model.TransacaoForm;
import com.dimdim.model.TransacaoResponse;
import com.dimdim.service.TransacaoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transacoes")
public class TransacaoApiController {

    private final TransacaoService service;

    public TransacaoApiController(TransacaoService service) {
        this.service = service;
    }

    @GetMapping
    public List<TransacaoResponse> listar() {
        return service.listar().stream().map(TransacaoResponse::de).toList();
    }

    @GetMapping("/{id}")
    public TransacaoResponse buscar(@PathVariable Long id) {
        return TransacaoResponse.de(service.buscar(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TransacaoResponse criar(@Valid @RequestBody TransacaoForm form) {
        return TransacaoResponse.de(service.criar(form));
    }

    @PutMapping("/{id}")
    public TransacaoResponse atualizar(@PathVariable Long id, @Valid @RequestBody TransacaoForm form) {
        return TransacaoResponse.de(service.atualizar(id, form));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) {
        service.excluir(id);
    }
}
