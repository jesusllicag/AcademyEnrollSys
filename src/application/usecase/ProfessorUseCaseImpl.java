package application.usecase;

import domain.model.Professor;
import domain.port.input.ProfessorUseCase;
import domain.port.output.ProfessorRepository;

import java.util.List;

public class ProfessorUseCaseImpl implements ProfessorUseCase {

    private final ProfessorRepository professorRepository;

    public ProfessorUseCaseImpl(ProfessorRepository professorRepository) {
        this.professorRepository = professorRepository;
    }

    @Override
    public Professor createProfessor(String name, String lastname) {
        String code = professorRepository.generateNextCode();
        String email = code + "@utp.edu.pe";
        Professor professor = new Professor(code, name, lastname, email);
        return professorRepository.save(professor);
    }

    @Override
    public Professor findByCode(String code) {
        return professorRepository.findByCode(code).orElse(null);
    }

    @Override
    public List<Professor> findAll() {
        return professorRepository.findAll();
    }

    @Override
    public boolean deleteProfessor(String code) {
        return professorRepository.delete(code);
    }
}
