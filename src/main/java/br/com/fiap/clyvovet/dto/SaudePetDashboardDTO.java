package br.com.fiap.clyvovet.dto;

import br.com.fiap.clyvovet.model.EventoSaude;
import br.com.fiap.clyvovet.model.Vacina;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Dashboard de saúde do pet — agrega timeline, alertas proativos e score de risco.
 * Representa a camada de inteligência da CLYVO VET.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaudePetDashboardDTO {

    private Long petId;
    private String petNome;
    private String especie;
    private String raca;
    private int idadeAnos;

    /** Score de risco calculado (0-100). Quanto maior, mais atenção o pet precisa. */
    private int scoreRisco;

    /** Classificação do risco: BAIXO, MEDIO, ALTO */
    private String classificacaoRisco;

    /** Total de consultas realizadas */
    private long totalConsultas;

    /** Dias desde a última consulta */
    private Long diasDesdeUltimaConsulta;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate ultimaConsulta;

    /** Alertas pendentes ordenados por urgência */
    private List<AlertaDTO> alertasPendentes;

    /** Vacinas com dose próxima ou vencida */
    private List<VacinaDTO.Response> vacinasPendentes;

    /** Medicamentos de uso contínuo ativos */
    private List<MedicamentoDTO.Response> medicamentosAtivos;

    /** Últimos eventos da timeline */
    private List<EventoSaudeDTO.Response> ultimosEventos;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AlertaDTO {
        private String tipo;
        private String descricao;
        private String prioridade; // URGENTE, ALTA, MEDIA, BAIXA
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate dataLimite;
    }
}
