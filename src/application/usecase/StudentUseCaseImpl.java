package application.usecase;

import domain.model.Student;
import domain.port.input.StudentUseCase;
import domain.port.output.StudentRepository;
import shared.datastructure.AVLTree;

import java.util.List;

public class StudentUseCaseImpl implements StudentUseCase {

    private final StudentRepository studentRepository;
    private final AVLTree<Student> studentTree;

    public StudentUseCaseImpl(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
        this.studentTree = new AVLTree<>(Student::getCode);
        // Cargar datos existentes en el arbol. Los codigos se generan e insertan
        // en orden correlativo ascendente (ver StudentRepositoryImpl.generateNextCode
        // y findAll ORDER BY code); un BST simple degeneraria en una lista enlazada
        // con este patron de carga, por lo que se usa AVL para garantizar O(log n).
        studentRepository.findAll().forEach(studentTree::insert);
    }

    @Override
    public Student createStudent(String name, String lastname, double gpa) {
        String code = studentRepository.generateNextCode();
        String email = code + "@utp.edu.pe";
        Student student = new Student(code, name, lastname, email, gpa);
        studentRepository.save(student);
        studentTree.insert(student);
        return student;
    }

    @Override
    public Student findByCode(String code) {
        // Busqueda eficiente usando el AVL en memoria (O(log n) garantizado)
        Student found = studentTree.search(code);
        if (found != null)
            return found;
        // Fallback a BD
        return studentRepository.findByCode(code).orElse(null);
    }

    @Override
    public Student findByEmail(String email) {
        return studentRepository.findByEmail(email).orElse(null);
    }

    @Override
    public List<Student> findAll() {
        // Recorrido inorden del AVL (ordenado por codigo)
        List<Student> fromTree = studentTree.inOrder();
        if (!fromTree.isEmpty())
            return fromTree;
        return studentRepository.findAll();
    }

    @Override
    public boolean deleteStudent(String code) {
        boolean deleted = studentRepository.delete(code);
        if (deleted)
            studentTree.delete(code);
        return deleted;
    }

    @Override
    public boolean updateGpa(String code, double gpa) {
        Student student = findByCode(code);
        if (student == null)
            return false;
        student.setGpa(gpa);
        boolean updated = studentRepository.update(student);
        if (updated)
            studentTree.update(student);
        return updated;
    }
}
