package domain.port.output;

import domain.model.Professor;
import java.util.List;
import java.util.Optional;

public interface ProfessorRepository {
    Professor save(Professor professor);

    Optional<Professor> findByCode(String code);

    List<Professor> findAll();

    boolean delete(String code);

    String generateNextCode();
}
