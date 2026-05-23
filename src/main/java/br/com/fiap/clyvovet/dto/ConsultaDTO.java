package br.com.fiap.clyvovet.dto;

import br.com.fiap.clyvovet.model.Consulta;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ConsultaDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {
        @NotNull @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate dataConsulta;

        @NotNull
        private Consulta.StatusConsulta status;

        @NotNull
        private Consulta.TipoConsulta tipoConsulta;

        @NotBlank @Size(max = 100)
        private String veterinario;

        @Size(max = 1000)
        private String diagnostico;

        @Size(max = 1000)
        private String prescricao;

        @Size(max = 500)
        private String observacoes;

        @DecimalMin("0.0")
        private Double valor;

        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate dataRetorno;

        @NotNull(message = "ID do pet é obrigatório")
        private Long petId;

        @NotNull(message = "ID da clínica é obrigatório")
        private Long clinicaId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long id;
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate dataConsulta;
        private Consulta.StatusConsulta status;
        private Consulta.TipoConsulta tipoConsulta;
        private String veterinario;
        private String diagnostico;
        private String prescricao;
        private String observacoes;
        private Double valor;
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate dataRetorno;
        private Long petId;
        private String petNome;
        private Long clinicaId;
        private String clinicaNome;
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime criadoEm;

        public static Response fromEntity(Consulta c) {
            return Response.builder()
                    .id(c.getId())
                    .dataConsulta(c.getDataConsulta())
                    .status(c.getStatus())
                    .tipoConsulta(c.getTipoConsulta())
                    .veterinario(c.getVeterinario())
                    .diagnostico(c.getDiagnostico())
                    .prescricao(c.getPrescricao())
                    .observacoes(c.getObservacoes())
                    .valor(c.getValor())
                    .dataRetorno(c.getDataRetorno())
                    .petId(c.getPet() != null ? c.getPet().getId() : null)
                    .petNome(c.getPet() != null ? c.getPet().getNome() : null)
                    .clinicaId(c.getClinica() != null ? c.getClinica().getId() : null)
                    .clinicaNome(c.getClinica() != null ? c.getClinica().getNome() : null)
                    .criadoEm(c.getCriadoEm())
                    .build();
        }
    }
}
