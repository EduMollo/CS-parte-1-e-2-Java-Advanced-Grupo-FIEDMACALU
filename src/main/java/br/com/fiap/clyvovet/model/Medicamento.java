package br.com.fiap.clyvovet.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "TB_MEDICAMENTO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Medicamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_medicamento")
    private Long id;

    @NotBlank(message = "Nome do medicamento é obrigatório")
    @Column(name = "nome", nullable = false, length = 100)
    private String nome;

    @Column(name = "principio_ativo", length = 100)
    private String principioAtivo;

    @NotBlank(message = "Dosagem é obrigatória")
    @Column(name = "dosagem", nullable = false, length = 100)
    private String dosagem;

    @NotBlank(message = "Frequência é obrigatória")
    @Column(name = "frequencia", nullable = false, length = 100)
    private String frequencia;

    @NotNull(message = "Data de início é obrigatória")
    @Column(name = "data_inicio", nullable = false)
    private LocalDate dataInicio;

    @Column(name = "data_fim")
    private LocalDate dataFim;

    @Builder.Default
    @Column(name = "uso_continuo", nullable = false)
    private boolean usoContinuo = false;

    @Column(name = "observacoes", length = 500)
    private String observacoes;

    @NotNull(message = "Pet é obrigatório")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pet", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Pet pet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_consulta")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Consulta consultaOrigem;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm;

    @PrePersist
    public void prePersist() {
        this.criadoEm = LocalDateTime.now();
    }
}
