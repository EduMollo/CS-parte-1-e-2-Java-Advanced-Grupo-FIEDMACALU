package br.com.fiap.clyvovet.repository;

import br.com.fiap.clyvovet.model.Vacina;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface VacinaRepository extends JpaRepository<Vacina, Long> {

    Page<Vacina> findByPetId(Long petId, Pageable pageable);

    List<Vacina> findByPetIdOrderByDataAplicacaoDesc(Long petId);

    @Query("SELECT v FROM Vacina v WHERE v.pet.id = :petId AND v.proximaDose <= :ate ORDER BY v.proximaDose ASC")
    List<Vacina> findVacinasPendentes(@Param("petId") Long petId, @Param("ate") LocalDate ate);

    @Query("SELECT v FROM Vacina v WHERE v.pet.tutor.id = :tutorId AND " +
           "v.proximaDose IS NOT NULL AND v.proximaDose <= :ate ORDER BY v.proximaDose ASC")
    List<Vacina> findVacinasPendentesByTutor(@Param("tutorId") Long tutorId, @Param("ate") LocalDate ate);
}
