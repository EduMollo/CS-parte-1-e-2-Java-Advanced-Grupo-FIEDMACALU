package br.com.fiap.clyvovet.repository;

import br.com.fiap.clyvovet.model.Consulta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ConsultaRepository extends JpaRepository<Consulta, Long> {

    Page<Consulta> findByPetId(Long petId, Pageable pageable);

    List<Consulta> findByPetIdOrderByDataConsultaDesc(Long petId);

    Page<Consulta> findByClinicaId(Long clinicaId, Pageable pageable);

    Page<Consulta> findByStatus(Consulta.StatusConsulta status, Pageable pageable);

    @Query("SELECT c FROM Consulta c WHERE c.pet.id = :petId ORDER BY c.dataConsulta DESC")
    List<Consulta> findHistoricoCompleto(@Param("petId") Long petId);

    @Query("SELECT c FROM Consulta c WHERE c.dataRetorno BETWEEN :inicio AND :fim AND " +
           "c.status NOT IN ('CANCELADA', 'REALIZADA')")
    List<Consulta> findRetornosPendentes(@Param("inicio") LocalDate inicio,
                                          @Param("fim") LocalDate fim);

    @Query("SELECT c FROM Consulta c WHERE c.pet.id = :petId AND " +
           "c.dataConsulta BETWEEN :inicio AND :fim ORDER BY c.dataConsulta DESC")
    List<Consulta> findByPetIdAndPeriodo(@Param("petId") Long petId,
                                          @Param("inicio") LocalDate inicio,
                                          @Param("fim") LocalDate fim);

    // Estatísticas para dashboard da clínica
    @Query("SELECT COUNT(c) FROM Consulta c WHERE c.clinica.id = :clinicaId AND " +
           "c.dataConsulta BETWEEN :inicio AND :fim")
    Long contarConsultasPorPeriodo(@Param("clinicaId") Long clinicaId,
                                    @Param("inicio") LocalDate inicio,
                                    @Param("fim") LocalDate fim);
}
