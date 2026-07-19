package application.usecase;

import domain.model.Professor;
import domain.port.input.ProfessorUseCase;
import domain.port.output.ProfessorRepository;
import shared.algorithm.SearchAlgorithms;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ProfessorUseCaseImpl implements ProfessorUseCase {

    private static final Comparator<Professor> BY_CODE = Comparator.comparing(Professor::getCode);

    private final ProfessorRepository professorRepository;

    // Cache en memoria ordenada por codigo (la consulta SQL ya trae ORDER BY
    // code), usada para resolver findByCode con busqueda binaria clasica.
    private List<Professor> professorCache;

    public ProfessorUseCaseImpl(ProfessorRepository professorRepository) {
        this.professorRepository = professorRepository;
        this.professorCache = new ArrayList<>(professorRepository.findAll());
    }

    @Override
    public Professor createProfessor(String name, String lastname) {
        String code = professorRepository.generateNextCode();
        String email = code + "@utp.edu.pe";
        Professor professor = new Professor(code, name, lastname, email);
        Professor saved = professorRepository.save(professor);
        refreshCache();
        return saved;
    }

    @Override
    public Professor findByCode(String code) {
        // Busqueda binaria clasica sobre la cache ordenada por codigo (O(log n)).
        // Se construye una "sonda" con el codigo buscado, ya que el comparador
        // solo evalua ese campo (misma tecnica que Collections.binarySearch).
        Professor probe = new Professor(code, null, null, null);
        int index = SearchAlgorithms.binarySearch(professorCache, probe, BY_CODE);
        if (index >= 0)
            return professorCache.get(index);
        // Fallback a BD por si la cache quedo desactualizada
        return professorRepository.findByCode(code).orElse(null);
    }

    @Override
    public List<Professor> findAll() {
        return professorRepository.findAll();
    }

    @Override
    public boolean deleteProfessor(String code) {
        boolean deleted = professorRepository.delete(code);
        if (deleted)
            refreshCache();
        return deleted;
    }

    private void refreshCache() {
        professorCache = new ArrayList<>(professorRepository.findAll());
    }
}
