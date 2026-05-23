package br.com.fiap.clyvovet.repository;

import br.com.fiap.clyvovet.model.Tutor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TutorRepository extends JpaRepository<Tutor, Long> {

    Optional<Tutor> findByCpf(String cpf);

    Optional<Tutor> findByEmail(String email);

    Page<Tutor> findByAtivoTrue(Pageable pageable);

    Page<Tutor> findByNomeContainingIgnoreCaseAndAtivoTrue(String nome, Pageable pageable);

    @Query("SELECT t FROM Tutor t WHERE t.ativo = true AND " +
           "(:nome IS NULL OR UPPER(t.nome) LIKE UPPER(CONCAT('%', :nome, '%'))) AND " +
           "(:email IS NULL OR UPPER(t.email) LIKE UPPER(CONCAT('%', :email, '%')))")
    Page<Tutor> buscarComFiltros(@Param("nome") String nome,
                                  @Param("email") String email,
                                  Pageable pageable);

    boolean existsByCpf(String cpf);

    boolean existsByEmail(String email);
}
