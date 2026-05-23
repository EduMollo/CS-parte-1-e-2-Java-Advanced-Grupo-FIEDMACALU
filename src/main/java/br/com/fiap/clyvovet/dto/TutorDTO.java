package br.com.fiap.clyvovet.dto;

import br.com.fiap.clyvovet.model.Tutor;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

public class TutorDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {
        @NotBlank(message = "Nome é obrigatório")
        @Size(min = 2, max = 100)
        private String nome;

        @NotBlank(message = "CPF é obrigatório")
        @Pattern(regexp = "\\d{11}", message = "CPF deve conter 11 dígitos numéricos")
        private String cpf;

        @NotBlank(message = "Email é obrigatório")
        @Email(message = "Email inválido")
        private String email;

        @NotBlank(message = "Telefone é obrigatório")
        @Pattern(regexp = "\\d{10,11}", message = "Telefone deve ter 10 ou 11 dígitos")
        private String telefone;

        @NotNull(message = "Data de nascimento é obrigatória")
        @Past(message = "Data de nascimento deve ser no passado")
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate dataNascimento;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long id;
        private String nome;
        private String cpf;
        private String email;
        private String telefone;
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate dataNascimento;
        private boolean ativo;
        private int totalPets;

        public static Response fromEntity(Tutor t) {
            return Response.builder()
                    .id(t.getId())
                    .nome(t.getNome())
                    .cpf(t.getCpf())
                    .email(t.getEmail())
                    .telefone(t.getTelefone())
                    .dataNascimento(t.getDataNascimento())
                    .ativo(t.isAtivo())
                    .totalPets(t.getPets() != null ? (int) t.getPets().stream().filter(p -> p.isAtivo()).count() : 0)
                    .build();
        }
    }
}
