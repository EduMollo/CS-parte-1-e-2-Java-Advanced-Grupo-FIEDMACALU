package br.com.fiap.clyvovet.service;

import br.com.fiap.clyvovet.dto.*;
import br.com.fiap.clyvovet.exception.ResourceNotFoundException;
import br.com.fiap.clyvovet.model.Consulta;
import br.com.fiap.clyvovet.model.Pet;
import br.com.fiap.clyvovet.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * Serviço de saúde inteligente da CLYVO VET.
 * Calcula score de risco e gera alertas proativos para cada pet.
 * Design Pattern: Strategy (cada regra de risco é encapsulada individualmente)
 */
@Service
@RequiredArgsConstructor
public class SaudePetService {

    private final PetRepository petRepository;
    private final ConsultaRepository consultaRepository;
    private final VacinaRepository vacinaRepository;
    private final MedicamentoRepository medicamentoRepository;
    private final EventoSaudeRepository eventoSaudeRepository;

    @Cacheable(value = "dashboard", key = "#petId")
    public SaudePetDashboardDTO gerarDashboard(Long petId) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new ResourceNotFoundException("Pet", petId));

        var historico = consultaRepository.findByPetIdOrderByDataConsultaDesc(petId);
        var vacinasPendentes = vacinaRepository.findVacinasPendentes(petId, LocalDate.now())
                .stream().map(VacinaDTO.Response::fromEntity).toList();
        var medicamentosAtivos = medicamentoRepository.findMedicamentosAtivos(petId)
                .stream().map(MedicamentoDTO.Response::fromEntity).toList();
        var ultimosEventos = eventoSaudeRepository.findByPetIdOrderByDataEventoDesc(petId)
                .stream().limit(10).map(EventoSaudeDTO.Response::fromEntity).toList();

        LocalDate ultimaConsultaData = historico.isEmpty() ? null : historico.get(0).getDataConsulta();
        Long diasDesdeUltima = ultimaConsultaData != null
                ? ChronoUnit.DAYS.between(ultimaConsultaData, LocalDate.now()) : null;

        List<SaudePetDashboardDTO.AlertaDTO> alertas = gerarAlertas(pet, diasDesdeUltima, vacinasPendentes, medicamentosAtivos);
        int score = calcularScore(pet, diasDesdeUltima, vacinasPendentes.size(), alertas);
        String classificacao = score >= 70 ? "ALTO" : score >= 40 ? "MEDIO" : "BAIXO";

        int idadeAnos = pet.getDataNascimento() != null
                ? Period.between(pet.getDataNascimento(), LocalDate.now()).getYears() : 0;

        return SaudePetDashboardDTO.builder()
                .petId(pet.getId())
                .petNome(pet.getNome())
                .especie(pet.getEspecie() != null ? pet.getEspecie().name() : null)
                .raca(pet.getRaca())
                .idadeAnos(idadeAnos)
                .scoreRisco(score)
                .classificacaoRisco(classificacao)
                .totalConsultas(historico.size())
                .ultimaConsulta(ultimaConsultaData)
                .diasDesdeUltimaConsulta(diasDesdeUltima)
                .alertasPendentes(alertas)
                .vacinasPendentes(vacinasPendentes)
                .medicamentosAtivos(medicamentosAtivos)
                .ultimosEventos(ultimosEventos)
                .build();
    }

    private List<SaudePetDashboardDTO.AlertaDTO> gerarAlertas(
            Pet pet, Long diasDesdeUltima,
            List<VacinaDTO.Response> vacinasPendentes,
            List<MedicamentoDTO.Response> medicamentosAtivos) {

        List<SaudePetDashboardDTO.AlertaDTO> alertas = new ArrayList<>();

        // Alerta: sem consulta há muito tempo
        if (diasDesdeUltima == null) {
            alertas.add(SaudePetDashboardDTO.AlertaDTO.builder()
                    .tipo("SEM_CONSULTA")
                    .descricao(pet.getNome() + " nunca teve uma consulta registrada.")
                    .prioridade("ALTA")
                    .dataLimite(LocalDate.now())
                    .build());
        } else if (diasDesdeUltima > 365) {
            alertas.add(SaudePetDashboardDTO.AlertaDTO.builder()
                    .tipo("CONSULTA_ATRASADA")
                    .descricao("Última consulta foi há " + diasDesdeUltima + " dias. Check-up anual recomendado.")
                    .prioridade(diasDesdeUltima > 540 ? "URGENTE" : "ALTA")
                    .dataLimite(LocalDate.now().plusDays(30))
                    .build());
        }

        // Alerta: vacinas vencidas/pendentes
        long vacinasVencidas = vacinasPendentes.stream().filter(VacinaDTO.Response::isVencida).count();
        if (vacinasVencidas > 0) {
            alertas.add(SaudePetDashboardDTO.AlertaDTO.builder()
                    .tipo("VACINA_VENCIDA")
                    .descricao(vacinasVencidas + " vacina(s) com validade expirada. Reforço necessário!")
                    .prioridade("URGENTE")
                    .dataLimite(LocalDate.now().plusDays(15))
                    .build());
        }

        long vacinasDoseProxima = vacinasPendentes.size() - vacinasVencidas;
        if (vacinasDoseProxima > 0) {
            alertas.add(SaudePetDashboardDTO.AlertaDTO.builder()
                    .tipo("DOSE_PROXIMA")
                    .descricao(vacinasDoseProxima + " vacina(s) com dose próxima prevista.")
                    .prioridade("MEDIA")
                    .dataLimite(LocalDate.now().plusDays(60))
                    .build());
        }

        // Alerta: pet idoso sem check-up recente
        int idadeAnos = pet.getDataNascimento() != null
                ? Period.between(pet.getDataNascimento(), LocalDate.now()).getYears() : 0;
        if (idadeAnos >= 7 && (diasDesdeUltima == null || diasDesdeUltima > 180)) {
            alertas.add(SaudePetDashboardDTO.AlertaDTO.builder()
                    .tipo("PET_SENIOR")
                    .descricao("Pet sênior (" + idadeAnos + " anos) sem check-up há mais de 6 meses. Exame semestral recomendado.")
                    .prioridade("ALTA")
                    .dataLimite(LocalDate.now().plusDays(30))
                    .build());
        }

        return alertas;
    }

    private int calcularScore(Pet pet, Long diasDesdeUltima,
                              int vacinasPendentes, List<SaudePetDashboardDTO.AlertaDTO> alertas) {
        int score = 0;

        // Consulta
        if (diasDesdeUltima == null) score += 40;
        else if (diasDesdeUltima > 540) score += 35;
        else if (diasDesdeUltima > 365) score += 25;
        else if (diasDesdeUltima > 180) score += 10;

        // Vacinas
        score += Math.min(vacinasPendentes * 10, 30);

        // Alertas urgentes
        long urgentes = alertas.stream().filter(a -> "URGENTE".equals(a.getPrioridade())).count();
        score += (int) urgentes * 10;

        // Idade
        int idadeAnos = pet.getDataNascimento() != null
                ? Period.between(pet.getDataNascimento(), LocalDate.now()).getYears() : 0;
        if (idadeAnos >= 10) score += 15;
        else if (idadeAnos >= 7) score += 10;

        return Math.min(score, 100);
    }
}
