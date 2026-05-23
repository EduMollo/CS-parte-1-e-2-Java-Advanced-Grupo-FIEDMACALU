package br.com.fiap.clyvovet.config;

import br.com.fiap.clyvovet.model.*;
import br.com.fiap.clyvovet.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * DataLoader — carrega dados de exemplo ao iniciar a aplicação.
 * Ativo por padrão (não usa @Profile para simplificar testes com H2).
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {

    private final TutorRepository tutorRepository;
    private final PetRepository petRepository;
    private final ClinicaRepository clinicaRepository;
    private final ConsultaRepository consultaRepository;
    private final VacinaRepository vacinaRepository;
    private final MedicamentoRepository medicamentoRepository;
    private final EventoSaudeRepository eventoSaudeRepository;

    @Override
    public void run(String... args) {
        if (tutorRepository.count() > 0) {
            log.info("Dados já carregados. Pulando DataLoader.");
            return;
        }

        log.info("Carregando dados de exemplo da CLYVO VET...");

        // Clínicas
        Clinica clinica1 = clinicaRepository.save(Clinica.builder()
                .nome("Clínica VetCare Premium")
                .cnpj("12345678000100")
                .telefone("1132001234")
                .email("contato@vetcare.com.br")
                .endereco("Av. Paulista, 1000")
                .cidade("São Paulo")
                .estado("SP")
                .ativo(true)
                .build());

        Clinica clinica2 = clinicaRepository.save(Clinica.builder()
                .nome("Hospital Veterinário PetLife")
                .cnpj("98765432000199")
                .telefone("2132009876")
                .email("hospital@petlife.com.br")
                .endereco("Rua das Flores, 200")
                .cidade("Rio de Janeiro")
                .estado("RJ")
                .ativo(true)
                .build());

        // Tutores
        Tutor tutor1 = tutorRepository.save(Tutor.builder()
                .nome("Ana Beatriz Silva")
                .cpf("12345678901")
                .email("ana.silva@email.com")
                .telefone("11987654321")
                .dataNascimento(LocalDate.of(1985, 3, 15))
                .ativo(true)
                .build());

        Tutor tutor2 = tutorRepository.save(Tutor.builder()
                .nome("Carlos Eduardo Mendes")
                .cpf("98765432100")
                .email("carlos.mendes@email.com")
                .telefone("21976543210")
                .dataNascimento(LocalDate.of(1990, 7, 22))
                .ativo(true)
                .build());

        Tutor tutor3 = tutorRepository.save(Tutor.builder()
                .nome("Mariana Costa Ferreira")
                .cpf("11122233344")
                .email("mariana.costa@email.com")
                .telefone("11965432198")
                .dataNascimento(LocalDate.of(1978, 11, 5))
                .ativo(true)
                .build());

        // Pets
        Pet pet1 = petRepository.save(Pet.builder()
                .nome("Bolinha")
                .especie(Pet.Especie.CACHORRO)
                .raca("Golden Retriever")
                .dataNascimento(LocalDate.of(2018, 6, 10))
                .pesoKg(28.5)
                .sexo(Pet.Sexo.MACHO)
                .castrado(true)
                .microchip("985112345678901")
                .tutor(tutor1)
                .clinicaPrincipal(clinica1)
                .ativo(true)
                .build());

        Pet pet2 = petRepository.save(Pet.builder()
                .nome("Mia")
                .especie(Pet.Especie.GATO)
                .raca("Persa")
                .dataNascimento(LocalDate.of(2020, 2, 14))
                .pesoKg(4.2)
                .sexo(Pet.Sexo.FEMEA)
                .castrado(true)
                .microchip("985198765432109")
                .tutor(tutor1)
                .clinicaPrincipal(clinica1)
                .ativo(true)
                .build());

        Pet pet3 = petRepository.save(Pet.builder()
                .nome("Thor")
                .especie(Pet.Especie.CACHORRO)
                .raca("Labrador")
                .dataNascimento(LocalDate.of(2015, 9, 30))
                .pesoKg(32.0)
                .sexo(Pet.Sexo.MACHO)
                .castrado(false)
                .microchip("985111111111111")
                .tutor(tutor2)
                .clinicaPrincipal(clinica2)
                .ativo(true)
                .build());

        Pet pet4 = petRepository.save(Pet.builder()
                .nome("Bella")
                .especie(Pet.Especie.CACHORRO)
                .raca("Poodle")
                .dataNascimento(LocalDate.of(2019, 4, 5))
                .pesoKg(6.8)
                .sexo(Pet.Sexo.FEMEA)
                .castrado(true)
                .tutor(tutor3)
                .clinicaPrincipal(clinica1)
                .ativo(true)
                .build());

        Pet pet5 = petRepository.save(Pet.builder()
                .nome("Simba")
                .especie(Pet.Especie.GATO)
                .raca("Maine Coon")
                .dataNascimento(LocalDate.of(2021, 8, 20))
                .pesoKg(7.5)
                .sexo(Pet.Sexo.MACHO)
                .castrado(true)
                .tutor(tutor3)
                .clinicaPrincipal(clinica2)
                .ativo(true)
                .build());

        // Consultas
        Consulta consulta1 = consultaRepository.save(Consulta.builder()
                .dataConsulta(LocalDate.of(2024, 11, 10))
                .status(Consulta.StatusConsulta.REALIZADA)
                .tipoConsulta(Consulta.TipoConsulta.ROTINA)
                .veterinario("Dr. Marcos Oliveira")
                .diagnostico("Check-up anual. Pet saudável.")
                .prescricao("Manter vermifugação em dia.")
                .valor(250.00)
                .pet(pet1)
                .clinica(clinica1)
                .build());

        Consulta consulta2 = consultaRepository.save(Consulta.builder()
                .dataConsulta(LocalDate.of(2025, 1, 15))
                .status(Consulta.StatusConsulta.REALIZADA)
                .tipoConsulta(Consulta.TipoConsulta.VACINACAO)
                .veterinario("Dra. Fernanda Lima")
                .diagnostico("Vacinação V10 e antirrábica aplicadas.")
                .valor(180.00)
                .dataRetorno(LocalDate.of(2026, 1, 15))
                .pet(pet1)
                .clinica(clinica1)
                .build());

        Consulta consulta3 = consultaRepository.save(Consulta.builder()
                .dataConsulta(LocalDate.now().plusDays(5))
                .status(Consulta.StatusConsulta.AGENDADA)
                .tipoConsulta(Consulta.TipoConsulta.PREVENTIVA)
                .veterinario("Dr. Ricardo Santos")
                .pet(pet3)
                .clinica(clinica2)
                .build());

        // Vacinas
        vacinaRepository.save(Vacina.builder()
                .nome("V10")
                .fabricante("Zoetis")
                .lote("LOT2024001")
                .dataAplicacao(LocalDate.now().minusMonths(4))
                .dataValidade(LocalDate.now().plusMonths(8))
                .proximaDose(LocalDate.now().plusMonths(8))
                .pet(pet1)
                .clinica(clinica1)
                .build());

        vacinaRepository.save(Vacina.builder()
                .nome("Antirrábica")
                .fabricante("MSD")
                .lote("LOT2024002")
                .dataAplicacao(LocalDate.now().minusMonths(4))
                .dataValidade(LocalDate.now().plusMonths(8))
                .proximaDose(LocalDate.now().plusMonths(8))
                .pet(pet1)
                .clinica(clinica1)
                .build());

        vacinaRepository.save(Vacina.builder()
                .nome("V4 Felina")
                .fabricante("Boehringer")
                .lote("LOT2024003")
                .dataAplicacao(LocalDate.now().minusYears(1))
                .dataValidade(LocalDate.now().plusMonths(1))
                .proximaDose(LocalDate.now().plusMonths(1))
                .pet(pet2)
                .clinica(clinica1)
                .build());

        // Medicamentos
        medicamentoRepository.save(Medicamento.builder()
                .nome("Simparic")
                .principioAtivo("Sarolaner")
                .dosagem("40mg")
                .frequencia("Mensal")
                .dataInicio(LocalDate.of(2025, 1, 1))
                .usoContinuo(true)
                .observacoes("Antipulgas e carrapatos. Aplicar no 1º dia de cada mês.")
                .pet(pet1)
                .consultaOrigem(consulta1)
                .build());

        medicamentoRepository.save(Medicamento.builder()
                .nome("Cosequin")
                .principioAtivo("Glucosamina + Condroitina")
                .dosagem("1 cápsula")
                .frequencia("Diária")
                .dataInicio(LocalDate.of(2024, 11, 10))
                .usoContinuo(true)
                .observacoes("Suporte articular para pet de grande porte.")
                .pet(pet3)
                .build());

        // Eventos de saúde
        eventoSaudeRepository.save(EventoSaude.builder()
                .tipoEvento(EventoSaude.TipoEvento.VACINA)
                .descricao("V10 e antirrábica aplicadas com sucesso.")
                .dataEvento(LocalDate.of(2025, 1, 15))
                .dataProximaAcao(LocalDate.of(2026, 1, 15))
                .concluido(true)
                .pet(pet1)
                .consulta(consulta2)
                .build());

        eventoSaudeRepository.save(EventoSaude.builder()
                .tipoEvento(EventoSaude.TipoEvento.CHECKUP)
                .descricao("Check-up anual realizado. Exames de sangue solicitados.")
                .dataEvento(LocalDate.of(2024, 11, 10))
                .dataProximaAcao(LocalDate.of(2025, 11, 10))
                .concluido(true)
                .pet(pet1)
                .consulta(consulta1)
                .build());

        eventoSaudeRepository.save(EventoSaude.builder()
                .tipoEvento(EventoSaude.TipoEvento.VERMIFUGACAO)
                .descricao("Vermifugação trimestral necessária.")
                .dataEvento(LocalDate.of(2024, 10, 1))
                .dataProximaAcao(LocalDate.of(2025, 1, 1))
                .concluido(false)
                .pet(pet2)
                .build());

        log.info("Dados de exemplo carregados: {} tutores, {} pets, {} clínicas.",
                tutorRepository.count(), petRepository.count(), clinicaRepository.count());
    }
}
