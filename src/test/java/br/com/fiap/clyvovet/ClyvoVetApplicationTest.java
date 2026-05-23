package br.com.fiap.clyvovet;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("ClyvoVetApplication - Smoke Test")
class ClyvoVetApplicationTest {

    @Test
    @DisplayName("Contexto Spring deve carregar sem erros")
    void contextLoads() {
        // Verifica que o contexto da aplicação inicializa corretamente
    }
}
