package br.com.fiap.clyvovet.dto;

import br.com.fiap.clyvovet.model.Medicamento;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

public class MedicamentoDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {
        @NotBlank @Size(max = 100)
        private String nome;

        @Size(max = 100)
        private String principioAtivo;

        @NotBlank @Size(max = 100)
        private String dosagem;

        @NotBlank @Size(max = 100)
        private String frequencia;

        @NotNull @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate dataInicio;

        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate dataFim;

        private boolean usoContinuo;

        @Size(max = 500)
        private String observacoes;

        @NotNull(message = "ID do pet é obrigatório")
        private Long petId;

        private Long consultaId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long id;
        private String nome;
        private String principioAtivo;
        private String dosagem;
        private String frequencia;
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate dataInicio;
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate dataFim;
        private boolean usoContinuo;
        private String observacoes;
        private Long petId;
        private String petNome;
        private Long consultaId;

        public static Response fromEntity(Medicamento m) {
            return Response.builder()
                    .id(m.getId())
                    .nome(m.getNome())
                    .principioAtivo(m.getPrincipioAtivo())
                    .dosagem(m.getDosagem())
                    .frequencia(m.getFrequencia())
                    .dataInicio(m.getDataInicio())
                    .dataFim(m.getDataFim())
                    .usoContinuo(m.isUsoContinuo())
                    .observacoes(m.getObservacoes())
                    .petId(m.getPet() != null ? m.getPet().getId() : null)
                    .petNome(m.getPet() != null ? m.getPet().getNome() : null)
                    .consultaId(m.getConsultaOrigem() != null ? m.getConsultaOrigem().getId() : null)
                    .build();
        }
    }
}
