package br.com.fiap.clyvovet.dto;

import br.com.fiap.clyvovet.model.Clinica;
import jakarta.validation.constraints.*;
import lombok.*;

public class ClinicaDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {
        @NotBlank @Size(min = 2, max = 150)
        private String nome;

        @NotBlank @Pattern(regexp = "\\d{14}", message = "CNPJ deve conter 14 dígitos numéricos")
        private String cnpj;

        @NotBlank @Pattern(regexp = "\\d{10,11}")
        private String telefone;

        @NotBlank @Email
        private String email;

        @NotBlank @Size(max = 300)
        private String endereco;

        @NotBlank @Size(max = 100)
        private String cidade;

        @NotBlank @Size(min = 2, max = 2)
        private String estado;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long id;
        private String nome;
        private String cnpj;
        private String telefone;
        private String email;
        private String endereco;
        private String cidade;
        private String estado;
        private boolean ativo;

        public static Response fromEntity(Clinica c) {
            return Response.builder()
                    .id(c.getId())
                    .nome(c.getNome())
                    .cnpj(c.getCnpj())
                    .telefone(c.getTelefone())
                    .email(c.getEmail())
                    .endereco(c.getEndereco())
                    .cidade(c.getCidade())
                    .estado(c.getEstado())
                    .ativo(c.isAtivo())
                    .build();
        }
    }
}
