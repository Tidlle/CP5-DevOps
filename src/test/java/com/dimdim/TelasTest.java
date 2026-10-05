package com.dimdim;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TelasTest {

    @Autowired MockMvc mvc;

    @Test
    void telas_renderizam_e_fluxo_web_e_api_funciona() throws Exception {
        mvc.perform(get("/")).andExpect(status().isOk());
        mvc.perform(get("/dashboard")).andExpect(status().isOk());   // sem contas: estado vazio

        mvc.perform(post("/contas").param("numero", "W-1").param("agencia", "0001")
                        .param("titular", "Web").param("saldo", "100"))
                .andExpect(status().is3xxRedirection());
        mvc.perform(post("/api/contas").contentType("application/json")
                        .content("{\"numero\":\"A-1\",\"agencia\":\"0001\",\"titular\":\"Api\",\"saldo\":50}"))
                .andExpect(status().isCreated());

        mvc.perform(get("/contas")).andExpect(status().isOk());
        mvc.perform(get("/contas/novo")).andExpect(status().isOk());
        mvc.perform(get("/contas/1/editar")).andExpect(status().isOk());
        mvc.perform(get("/transacoes/novo")).andExpect(status().isOk());

        mvc.perform(post("/transacoes").param("contaId", "1").param("tipo", "DEPOSITO")
                        .param("valor", "10").param("descricao", "teste"))
                .andExpect(status().is3xxRedirection());
        mvc.perform(get("/transacoes")).andExpect(status().isOk());
        mvc.perform(get("/dashboard")).andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Saldo total")));
        mvc.perform(get("/transacoes/1/editar")).andExpect(status().isOk());
        mvc.perform(get("/api/transacoes")).andExpect(status().isOk());
        mvc.perform(get("/api/contas/999")).andExpect(status().isNotFound());
        mvc.perform(post("/transacoes").param("contaId", "1").param("tipo", "SAQUE").param("valor", "9999"))
                .andExpect(status().isOk());
    }
}
