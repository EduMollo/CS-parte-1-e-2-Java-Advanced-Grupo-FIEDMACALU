package br.com.fiap.clyvovet.repository;

import br.com.fiap.clyvovet.model.Medicamento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedicamentoRepository extends JpaRepository<Medicamento, Long> {

    Page<Medicamento> findByPetId(Long petId, Pageable pageable);

    List<Medicamento> findByPetIdOrderByCriadoEmDesc(Long petId);

    // Medicamentos de uso contínuo ativos
    @Query("SELECT m FROM Medicamento m WHERE m.pet.id = :petId AND m.usoContinuo = true AND " +
           "(m.dataFim IS NULL OR m.dataFim >= CURRENT_DATE)")
    List<Medicamento> findMedicamentosContiuosAtivos(@Param("petId") Long petId);

    // Todos os medicamentos ativos do pet (contínuos + vigentes)
    @Query("SELECT m FROM Medicamento m WHERE m.pet.id = :petId AND " +
           "(m.usoContinuo = true OR (m.dataFim IS NULL OR m.dataFim >= CURRENT_DATE)) " +
           "ORDER BY m.dataInicio DESC")
    List<Medicamento> findMedicamentosAtivos(@Param("petId") Long petId);
}
