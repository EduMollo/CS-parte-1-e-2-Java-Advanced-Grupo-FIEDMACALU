package br.com.fiap.clyvovet.dto;

import br.com.fiap.clyvovet.model.EventoSaude;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class EventoSaudeDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {
        @NotNull
        private EventoSaude.TipoEvento tipoEvento;

        @NotBlank @Size(min = 5, max = 500)
        private String descricao;

        @NotNull @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate dataEvento;

        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate dataProximaAcao;

        private boolean concluido;

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
        private EventoSaude.TipoEvento tipoEvento;
        private String descricao;
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate dataEvento;
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate dataProximaAcao;
        private boolean concluido;
        private Long petId;
        private String petNome;
        private Long consultaId;
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime criadoEm;

        public static Response fromEntity(EventoSaude e) {
            return Response.builder()
                    .id(e.getId())
                    .tipoEvento(e.getTipoEvento())
                    .descricao(e.getDescricao())
                    .dataEvento(e.getDataEvento())
                    .dataProximaAcao(e.getDataProximaAcao())
                    .concluido(e.isConcluido())
                    .petId(e.getPet() != null ? e.getPet().getId() : null)
                    .petNome(e.getPet() != null ? e.getPet().getNome() : null)
                    .consultaId(e.getConsulta() != null ? e.getConsulta().getId() : null)
                    .criadoEm(e.getCriadoEm())
                    .build();
        }
    }
}
