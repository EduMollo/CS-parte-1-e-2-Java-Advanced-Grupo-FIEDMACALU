package br.com.fiap.clyvovet.dto;

import br.com.fiap.clyvovet.model.Pet;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.time.Period;

public class PetDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {
        @NotBlank(message = "Nome do pet é obrigatório")
        @Size(min = 1, max = 80)
        private String nome;

        @NotNull(message = "Espécie é obrigatória")
        private Pet.Especie especie;

        @NotBlank(message = "Raça é obrigatória")
        @Size(max = 80)
        private String raca;

        @NotNull(message = "Data de nascimento é obrigatória")
        @Past
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate dataNascimento;

        @DecimalMin("0.1") @DecimalMax("200.0")
        private Double pesoKg;

        private Pet.Sexo sexo;
        private Boolean castrado;

        @Size(max = 20)
        private String microchip;

        @Size(max = 500)
        private String observacoes;

        @NotNull(message = "ID do tutor é obrigatório")
        private Long tutorId;

        private Long clinicaId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long id;
        private String nome;
        private Pet.Especie especie;
        private String raca;
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate dataNascimento;
        private int idadeAnos;
        private Double pesoKg;
        private Pet.Sexo sexo;
        private Boolean castrado;
        private String microchip;
        private String observacoes;
        private boolean ativo;
        private Long tutorId;
        private String tutorNome;
        private Long clinicaId;
        private String clinicaNome;

        public static Response fromEntity(Pet p) {
            int idade = p.getDataNascimento() != null
                    ? Period.between(p.getDataNascimento(), LocalDate.now()).getYears()
                    : 0;
            return Response.builder()
                    .id(p.getId())
                    .nome(p.getNome())
                    .especie(p.getEspecie())
                    .raca(p.getRaca())
                    .dataNascimento(p.getDataNascimento())
                    .idadeAnos(idade)
                    .pesoKg(p.getPesoKg())
                    .sexo(p.getSexo())
                    .castrado(p.getCastrado())
                    .microchip(p.getMicrochip())
                    .observacoes(p.getObservacoes())
                    .ativo(p.isAtivo())
                    .tutorId(p.getTutor() != null ? p.getTutor().getId() : null)
                    .tutorNome(p.getTutor() != null ? p.getTutor().getNome() : null)
                    .clinicaId(p.getClinicaPrincipal() != null ? p.getClinicaPrincipal().getId() : null)
                    .clinicaNome(p.getClinicaPrincipal() != null ? p.getClinicaPrincipal().getNome() : null)
                    .build();
        }
    }
}
