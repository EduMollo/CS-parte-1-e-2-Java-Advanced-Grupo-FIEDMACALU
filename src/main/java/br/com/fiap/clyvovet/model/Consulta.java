package br.com.fiap.clyvovet.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "TB_CONSULTA")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Consulta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_consulta")
    private Long id;

    @NotNull(message = "Data da consulta é obrigatória")
    @Column(name = "data_consulta", nullable = false)
    private LocalDate dataConsulta;

    @NotNull(message = "Status é obrigatório")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private StatusConsulta status;

    @NotNull(message = "Tipo da consulta é obrigatório")
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_consulta", nullable = false, length = 30)
    private TipoConsulta tipoConsulta;

    @NotBlank(message = "Nome do veterinário é obrigatório")
    @Column(name = "veterinario", nullable = false, length = 100)
    private String veterinario;

    @Column(name = "diagnostico", length = 1000)
    private String diagnostico;

    @Column(name = "prescricao", length = 1000)
    private String prescricao;

    @Column(name = "observacoes", length = 500)
    private String observacoes;

    @DecimalMin(value = "0.0", message = "Valor não pode ser negativo")
    @Column(name = "valor")
    private Double valor;

    @Column(name = "data_retorno")
    private LocalDate dataRetorno;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm;

    @NotNull(message = "Pet é obrigatório")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pet", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Pet pet;

    @NotNull(message = "Clínica é obrigatória")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_clinica", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Clinica clinica;

    @OneToMany(mappedBy = "consulta", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Builder.Default
    private List<EventoSaude> eventos = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        this.criadoEm = LocalDateTime.now();
    }

    public enum StatusConsulta {
        AGENDADA, CONFIRMADA, REALIZADA, CANCELADA, NAO_COMPARECEU
    }

    public enum TipoConsulta {
        ROTINA, EMERGENCIA, RETORNO, CIRURGIA, VACINACAO, EXAME, PREVENTIVA
    }
}
