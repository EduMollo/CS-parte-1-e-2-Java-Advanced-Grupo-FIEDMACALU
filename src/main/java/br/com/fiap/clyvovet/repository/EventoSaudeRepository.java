package br.com.fiap.clyvovet.repository;

import br.com.fiap.clyvovet.model.EventoSaude;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EventoSaudeRepository extends JpaRepository<EventoSaude, Long> {

    Page<EventoSaude> findByPetId(Long petId, Pageable pageable);

    List<EventoSaude> findByPetIdOrderByDataEventoDesc(Long petId);

    List<EventoSaude> findByPetIdAndTipoEvento(Long petId, EventoSaude.TipoEvento tipo);

    // Timeline do pet - histórico completo ordenado
    @Query("SELECT e FROM EventoSaude e WHERE e.pet.id = :petId ORDER BY e.dataEvento DESC")
    List<EventoSaude> findTimelinePet(@Param("petId") Long petId);

    // Próximas ações pendentes (alertas proativos)
    @Query("SELECT e FROM EventoSaude e WHERE e.pet.id = :petId AND " +
           "e.dataProximaAcao IS NOT NULL AND e.dataProximaAcao <= :ate AND " +
           "e.concluido = false ORDER BY e.dataProximaAcao ASC")
    List<EventoSaude> findAlertasPendentes(@Param("petId") Long petId,
                                            @Param("ate") LocalDate ate);

    // Alertas de todos os pets de um tutor
    @Query("SELECT e FROM EventoSaude e WHERE e.pet.tutor.id = :tutorId AND " +
           "e.dataProximaAcao IS NOT NULL AND e.dataProximaAcao <= :ate AND " +
           "e.concluido = false ORDER BY e.dataProximaAcao ASC")
    List<EventoSaude> findAlertasByTutor(@Param("tutorId") Long tutorId,
                                          @Param("ate") LocalDate ate);
}
