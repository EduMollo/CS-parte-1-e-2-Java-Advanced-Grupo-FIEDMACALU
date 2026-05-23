package br.com.fiap.clyvovet.dto;

import br.com.fiap.clyvovet.model.Vacina;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

public class VacinaDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {
        @NotBlank @Size(max = 100)
        private String nome;

        @NotBlank @Size(max = 100)
        private String fabricante;

        @Size(max = 50)
        private String lote;

        @NotNull @PastOrPresent @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate dataAplicacao;

        @NotNull @Future @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate dataValidade;

        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate proximaDose;

        @NotNull(message = "ID do pet é obrigatório")
        private Long petId;

        private Long clinicaId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long id;
        private String nome;
        private String fabricante;
        private String lote;
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate dataAplicacao;
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate dataValidade;
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate proximaDose;
        private Long petId;
        private String petNome;
        private Long clinicaId;
        private String clinicaNome;
        private boolean vencida;

        public static Response fromEntity(Vacina v) {
            return Response.builder()
                    .id(v.getId())
                    .nome(v.getNome())
                    .fabricante(v.getFabricante())
                    .lote(v.getLote())
                    .dataAplicacao(v.getDataAplicacao())
                    .dataValidade(v.getDataValidade())
                    .proximaDose(v.getProximaDose())
                    .petId(v.getPet() != null ? v.getPet().getId() : null)
                    .petNome(v.getPet() != null ? v.getPet().getNome() : null)
                    .clinicaId(v.getClinica() != null ? v.getClinica().getId() : null)
                    .clinicaNome(v.getClinica() != null ? v.getClinica().getNome() : null)
                    .vencida(v.getDataValidade() != null && v.getDataValidade().isBefore(LocalDate.now()))
                    .build();
        }
    }
}
