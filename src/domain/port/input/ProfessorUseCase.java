package domain.port.input;

import domain.model.Professor;
import java.util.List;

public interface ProfessorUseCase {
    Professor createProfessor(String name, String lastname);

    Professor findByCode(String code);

    List<Professor> findAll();

    boolean deleteProfessor(String code);
}
