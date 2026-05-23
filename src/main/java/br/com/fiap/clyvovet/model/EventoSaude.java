package br.com.fiap.clyvovet.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "TB_EVENTO_SAUDE")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventoSaude {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_evento")
    private Long id;

    @NotNull(message = "Tipo do evento é obrigatório")
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_evento", nullable = false, length = 30)
    private TipoEvento tipoEvento;

    @NotBlank(message = "Descrição é obrigatória")
    @Size(min = 5, max = 500, message = "Descrição deve ter entre 5 e 500 caracteres")
    @Column(name = "descricao", nullable = false, length = 500)
    private String descricao;

    @NotNull(message = "Data do evento é obrigatória")
    @Column(name = "data_evento", nullable = false)
    private LocalDate dataEvento;

    @Column(name = "data_proxima_acao")
    private LocalDate dataProximaAcao;

    @Builder.Default
    @Column(name = "concluido", nullable = false)
    private boolean concluido = false;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm;

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
    private Consulta consulta;

    @PrePersist
    public void prePersist() {
        this.criadoEm = LocalDateTime.now();
    }

    public enum TipoEvento {
        VACINA,
        VERMIFUGACAO,
        CHECKUP,
        MEDICAMENTO,
        CIRURGIA,
        EXAME,
        RETORNO,
        EMERGENCIA,
        NUTRICAO,
        COMPORTAMENTO,
        OUTRO
    }
}
