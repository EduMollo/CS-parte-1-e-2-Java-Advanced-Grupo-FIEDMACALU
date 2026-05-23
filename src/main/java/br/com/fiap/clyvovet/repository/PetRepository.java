package br.com.fiap.clyvovet.repository;

import br.com.fiap.clyvovet.model.Pet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PetRepository extends JpaRepository<Pet, Long> {

    Page<Pet> findByAtivoTrue(Pageable pageable);

    List<Pet> findByTutorIdAndAtivoTrue(Long tutorId);

    Page<Pet> findByTutorIdAndAtivoTrue(Long tutorId, Pageable pageable);

    Optional<Pet> findByMicrochip(String microchip);

    @Query("SELECT p FROM Pet p WHERE p.ativo = true AND " +
           "(:nome IS NULL OR UPPER(p.nome) LIKE UPPER(CONCAT('%', :nome, '%'))) AND " +
           "(:especie IS NULL OR p.especie = :especie) AND " +
           "(:raca IS NULL OR UPPER(p.raca) LIKE UPPER(CONCAT('%', :raca, '%')))")
    Page<Pet> buscarComFiltros(@Param("nome") String nome,
                                @Param("especie") Pet.Especie especie,
                                @Param("raca") String raca,
                                Pageable pageable);

    // Pets com vacinas vencidas (usado para alertas proativos)
    @Query("SELECT DISTINCT p FROM Pet p " +
           "JOIN p.eventos e " +
           "WHERE p.ativo = true AND e.tipoEvento = 'VACINA' AND " +
           "e.dataProximaAcao < CURRENT_DATE AND e.concluido = false")
    List<Pet> findPetsComVacinasVencidas();

    // Pets sem consulta há mais de 365 dias (score de risco)
    @Query("SELECT p FROM Pet p WHERE p.ativo = true AND " +
           "((SELECT MAX(c.dataConsulta) FROM Consulta c WHERE c.pet.id = p.id) < :cutoffDate OR " +
           "(SELECT COUNT(c) FROM Consulta c WHERE c.pet.id = p.id) = 0)")
    List<Pet> findPetsSemConsultaRecente(@Param("cutoffDate") java.time.LocalDate cutoffDate);

    long countByAtivoTrue();
}
