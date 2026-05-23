package br.com.fiap.clyvovet.repository;

import br.com.fiap.clyvovet.model.Clinica;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClinicaRepository extends JpaRepository<Clinica, Long> {

    Optional<Clinica> findByCnpj(String cnpj);

    Page<Clinica> findByAtivoTrue(Pageable pageable);

    Page<Clinica> findByCidadeIgnoreCaseAndAtivoTrue(String cidade, Pageable pageable);

    Page<Clinica> findByEstadoIgnoreCaseAndAtivoTrue(String estado, Pageable pageable);

    @Query("SELECT c FROM Clinica c WHERE c.ativo = true AND " +
           "(:nome IS NULL OR UPPER(c.nome) LIKE UPPER(CONCAT('%', :nome, '%'))) AND " +
           "(:cidade IS NULL OR UPPER(c.cidade) LIKE UPPER(CONCAT('%', :cidade, '%'))) AND " +
           "(:estado IS NULL OR UPPER(c.estado) = UPPER(:estado))")
    Page<Clinica> buscarComFiltros(@Param("nome") String nome,
                                    @Param("cidade") String cidade,
                                    @Param("estado") String estado,
                                    Pageable pageable);

    boolean existsByCnpj(String cnpj);
}
