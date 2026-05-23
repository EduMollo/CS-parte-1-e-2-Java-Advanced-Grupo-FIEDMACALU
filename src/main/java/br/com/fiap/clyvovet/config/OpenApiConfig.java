package br.com.fiap.clyvovet.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
    info = @Info(
        title = "CLYVO VET API",
        version = "1.0.0",
        description = """
            API RESTful da plataforma CLYVO VET — Jornada Contínua de Saúde Pet.
            Desenvolvida para o Challenge FIAP 2026 como parte da disciplina Java Advanced.
            
            Funcionalidades:
            - Cadastro de tutores, pets e clínicas veterinárias
            - Prontuário digital completo com histórico de consultas
            - Carteira de vacinação digital
            - Controle de medicamentos e prescrições
            - Timeline de saúde do pet
            - Dashboard inteligente com score de risco e alertas proativos
            """,
        contact = @Contact(
            name = "Equipe FIAP Challenge 2026",
            email = "clyvovet@fiap.com.br"
        ),
        license = @License(name = "Apache 2.0", url = "https://www.apache.org/licenses/LICENSE-2.0")
    ),
    servers = {
        @Server(url = "http://localhost:8080", description = "Ambiente de Desenvolvimento")
    }
)
@Configuration
public class OpenApiConfig {
}
