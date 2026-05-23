package br.com.fiap.clyvovet.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "TB_PET")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pet")
    private Long id;

    @NotBlank(message = "Nome do pet é obrigatório")
    @Size(min = 1, max = 80, message = "Nome deve ter entre 1 e 80 caracteres")
    @Column(name = "nome", nullable = false, length = 80)
    private String nome;

    @NotNull(message = "Espécie é obrigatória")
    @Enumerated(EnumType.STRING)
    @Column(name = "especie", nullable = false, length = 20)
    private Especie especie;

    @NotBlank(message = "Raça é obrigatória")
    @Size(max = 80)
    @Column(name = "raca", nullable = false, length = 80)
    private String raca;

    @NotNull(message = "Data de nascimento é obrigatória")
    @Past(message = "Data de nascimento deve ser no passado")
    @Column(name = "data_nascimento", nullable = false)
    private LocalDate dataNascimento;

    @DecimalMin(value = "0.1", message = "Peso deve ser maior que 0.1 kg")
    @DecimalMax(value = "200.0", message = "Peso deve ser menor que 200 kg")
    @Column(name = "peso_kg")
    private Double pesoKg;

    @Enumerated(EnumType.STRING)
    @Column(name = "sexo", length = 10)
    private Sexo sexo;

    @Column(name = "castrado")
    private Boolean castrado;

    @Column(name = "microchip", unique = true, length = 20)
    private String microchip;

    @Column(name = "observacoes", length = 500)
    private String observacoes;

    @Builder.Default
    @Column(name = "ativo", nullable = false)
    private boolean ativo = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tutor", nullable = false)
    @NotNull(message = "Tutor é obrigatório")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Tutor tutor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_clinica")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Clinica clinicaPrincipal;

    @OneToMany(mappedBy = "pet", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Builder.Default
    private List<EventoSaude> eventos = new ArrayList<>();

    @OneToMany(mappedBy = "pet", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Builder.Default
    private List<Consulta> consultas = new ArrayList<>();

    public enum Especie {
        CACHORRO, GATO, PASSARO, ROEDOR, REPTIL, OUTRO
    }

    public enum Sexo {
        MACHO, FEMEA
    }
}
