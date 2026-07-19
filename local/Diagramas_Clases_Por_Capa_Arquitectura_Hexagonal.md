# Diagramas de Clases por Capa — Arquitectura Hexagonal

Este documento descompone el diagrama de clases general del sistema (sección 4.2 del informe técnico) en vistas parciales, una por cada capa de la Arquitectura Hexagonal (*Ports and Adapters*, Cockburn 2005). El objetivo es facilitar la lectura del diseño en dos niveles:

1. **Vista aislada**: cada capa mostrada únicamente con sus relaciones internas.
2. **Vista con relaciones cruzadas**: cada capa mostrada junto con las clases de las capas con las que interactúa directamente.

Al final se incluye una **vista global reducida**, pensada como diagrama de portada del capítulo, con solo las clases más representativas de cada capa.

---

## Parte 1: Diagramas aislados por capa

### 1.1 Capa de Dominio (aislada)

```mermaid
classDiagram
    direction LR

    namespace Dominio_Modelo {
        class Student {
            -String code
            -String name
            -String lastname
            -String email
            -double gpa
            +compareTo(Student) int
        }
        class Professor {
            -String code
            -String name
            -String lastname
        }
        class Course {
            -String code
            -String name
            -int maxStudents
            -boolean enrollmentOpen
        }
        class Classroom {
            -String code
            -int capacity
        }
        class Enrollment {
            -String studentCode
            -String courseCode
            -LocalDateTime enrolledAt
        }
        class EnrollmentPeriod {
            -boolean active
            -int currentServingPosition
        }
        class EnrollmentQueueEntry {
            -String studentCode
            -double gpa
            -int position
            -LocalDateTime registeredAt
            +compareTo(EnrollmentQueueEntry) int
        }
    }

    namespace Dominio_Puertos_Entrada {
        class EnrollmentUseCase {
            <<interface>>
            +joinQueue(String) boolean
            +openEnrollment() boolean
            +enrollStudent(String, String) boolean
            +isStudentTurn(String) boolean
            +advanceQueue() void
        }
        class StudentUseCase {
            <<interface>>
            +findByCode(String) Student
            +createStudent(...) Student
        }
        class CourseUseCase {
            <<interface>>
            +findByCode(String) Course
        }
        class ProfessorUseCase {
            <<interface>>
            +findByCode(String) Professor
        }
        class AdminUseCase {
            <<interface>>
        }
    }

    namespace Dominio_Puertos_Salida {
        class EnrollmentQueueRepository {
            <<interface>>
            +add(EnrollmentQueueEntry) boolean
            +findAll() List
            +updatePositions(List) boolean
            +getMaxPosition() int
        }
        class EnrollmentRepository {
            <<interface>>
        }
        class EnrollmentPeriodRepository {
            <<interface>>
        }
        class StudentRepository {
            <<interface>>
            +findByCode(String) Optional
            +findAll() List
            +generateNextCode() String
        }
        class CourseRepository {
            <<interface>>
            +findByCode(String) Optional
            +findAll() List
        }
        class ProfessorRepository {
            <<interface>>
            +findByCode(String) Optional
            +findAll() List
        }
    }

    EnrollmentQueueRepository ..> EnrollmentQueueEntry : maneja
    StudentRepository ..> Student : maneja
    CourseRepository ..> Course : maneja
    ProfessorRepository ..> Professor : maneja
    EnrollmentUseCase ..> Enrollment : maneja
    EnrollmentUseCase ..> EnrollmentPeriod : maneja
    StudentUseCase ..> Student : maneja
    CourseUseCase ..> Course : maneja
    ProfessorUseCase ..> Professor : maneja
```

> **Nota:** dentro de la capa de dominio no existen dependencias de implementación (`..|>`) ni de inyección (`-->`), porque esas relaciones cruzan hacia aplicación e infraestructura. Aquí solo aparecen las relaciones de "manejo" entre puertos y entidades, que sí son internas al dominio.

---

### 1.2 Capa de Aplicación (aislada)

```mermaid
classDiagram
    direction LR

    namespace Capa_Aplicacion {
        class EnrollmentUseCaseImpl {
            -EnrollmentQueueRepository queueRepository
            -EnrollmentRepository enrollmentRepository
            -EnrollmentPeriodRepository periodRepository
            -CourseRepository courseRepository
            -StudentRepository studentRepository
            +openEnrollment() boolean
            +joinQueue(String) boolean
            +enrollStudent(String, String) boolean
        }
        class StudentUseCaseImpl {
            -StudentRepository studentRepository
            -AVLTree~Student~ studentTree
            +findByCode(String) Student
        }
        class CourseUseCaseImpl {
            -CourseRepository courseRepository
            -List~Course~ courseCache
            +findByCode(String) Course
        }
        class ProfessorUseCaseImpl {
            -ProfessorRepository professorRepository
            -List~Professor~ professorCache
            +findByCode(String) Professor
        }
    }
```

> **Nota:** esta capa, vista de forma aislada, es prácticamente una lista de clases sin relaciones entre sí. Su naturaleza es depender exclusivamente de abstracciones del dominio (puertos de salida) y de utilidades compartidas — por eso no existen aristas internas propias de la capa de aplicación.

---

### 1.3 Capa de Infraestructura (aislada)

```mermaid
classDiagram
    direction LR

    namespace Infraestructura_Persistencia {
        class EnrollmentQueueRepositoryImpl {
            -DatabaseConnection db
        }
        class EnrollmentRepositoryImpl {
            -DatabaseConnection db
        }
        class EnrollmentPeriodRepositoryImpl {
            -DatabaseConnection db
        }
        class StudentRepositoryImpl {
            -DatabaseConnection db
        }
        class CourseRepositoryImpl {
            -DatabaseConnection db
        }
        class ProfessorRepositoryImpl {
            -DatabaseConnection db
        }
        class DatabaseConnection {
            <<Singleton>>
            -Connection connection
            +getInstance() DatabaseConnection
        }
    }

    namespace Infraestructura_UI {
        class MainConsole
        class StudentConsole
        class AdminConsole
        class ConsoleHelper {
            <<utility>>
        }
    }

    EnrollmentQueueRepositoryImpl --> DatabaseConnection : usa
    EnrollmentRepositoryImpl --> DatabaseConnection : usa
    EnrollmentPeriodRepositoryImpl --> DatabaseConnection : usa
    StudentRepositoryImpl --> DatabaseConnection : usa
    CourseRepositoryImpl --> DatabaseConnection : usa
    ProfessorRepositoryImpl --> DatabaseConnection : usa

    MainConsole --> StudentConsole : orquesta
    MainConsole --> AdminConsole : orquesta
    StudentConsole --> ConsoleHelper : usa
    AdminConsole --> ConsoleHelper : usa
```

---

### 1.4 Capa Compartida / Transversal (aislada)

```mermaid
classDiagram
    direction LR

    namespace Compartido_Estructuras {
        class AVLTree~T~ {
            -Node~T~ root
            -Function~T,String~ keyExtractor
            +insert(T) void
            +search(String) T
            +delete(String) boolean
            +inOrder() List~T~
            -rebalance(Node~T~) Node~T~
        }
        class Node~T~ {
            -T value
            -Node~T~ left
            -Node~T~ right
            -int height
        }
        class CustomLinkedList~T~ {
            -Node~T~ head
            -int size
            +addFirst(T) void
            +addLast(T) void
            +removeFirst() T
        }
        class SortingAlgorithms {
            <<utility>>
            +insertionSort(List, Comparator) void
            +bubbleSort(List, Comparator) void
            +quickSort(List, int, int, Comparator) void
        }
        class SearchAlgorithms {
            <<utility>>
            +sequentialSearch(List, Predicate) T
            +binarySearch(List, T, Comparator) int
            +binarySearchByKey(List, String, Function) T
        }
    }

    AVLTree --> Node : compuesto por
```

> **Nota:** `SortingAlgorithms` y `SearchAlgorithms` son utilidades sin estado y sin relación estructural con `AVLTree` o `CustomLinkedList`; por eso quedan sueltas en el diagrama aislado.

---

## Parte 2: Diagramas por capa con relaciones hacia otras capas

### 2.1 Dominio ↔ (Aplicación + Infraestructura)

```mermaid
classDiagram
    direction LR

    namespace Dominio_Puertos_Entrada {
        class EnrollmentUseCase { <<interface>> }
        class StudentUseCase { <<interface>> }
        class CourseUseCase { <<interface>> }
        class ProfessorUseCase { <<interface>> }
    }

    namespace Dominio_Puertos_Salida {
        class EnrollmentQueueRepository { <<interface>> }
        class StudentRepository { <<interface>> }
        class CourseRepository { <<interface>> }
        class ProfessorRepository { <<interface>> }
    }

    namespace Aplicacion_Externa {
        class EnrollmentUseCaseImpl
        class StudentUseCaseImpl
        class CourseUseCaseImpl
        class ProfessorUseCaseImpl
    }

    namespace Infraestructura_Externa {
        class EnrollmentQueueRepositoryImpl
        class StudentRepositoryImpl
        class CourseRepositoryImpl
        class ProfessorRepositoryImpl
        class StudentConsole
    }

    EnrollmentUseCaseImpl ..|> EnrollmentUseCase : implementa
    StudentUseCaseImpl ..|> StudentUseCase : implementa
    CourseUseCaseImpl ..|> CourseUseCase : implementa
    ProfessorUseCaseImpl ..|> ProfessorUseCase : implementa

    EnrollmentQueueRepositoryImpl ..|> EnrollmentQueueRepository : implementa
    StudentRepositoryImpl ..|> StudentRepository : implementa
    CourseRepositoryImpl ..|> CourseRepository : implementa
    ProfessorRepositoryImpl ..|> ProfessorRepository : implementa

    StudentConsole --> StudentUseCase : consume puerto de entrada
```

> El dominio es el eje de la inversión de dependencias: recibe implementaciones desde aplicación (puertos de entrada) e infraestructura (puertos de salida), pero nunca las referencia directamente en su propio código — las flechas de implementación siempre "apuntan hacia" el dominio.

---

### 2.2 Aplicación ↔ (Dominio + Compartido)

```mermaid
classDiagram
    direction LR

    namespace Capa_Aplicacion {
        class EnrollmentUseCaseImpl {
            -EnrollmentQueueRepository queueRepository
            -CourseRepository courseRepository
            -StudentRepository studentRepository
        }
        class StudentUseCaseImpl {
            -StudentRepository studentRepository
            -AVLTree~Student~ studentTree
        }
        class CourseUseCaseImpl {
            -CourseRepository courseRepository
            -List~Course~ courseCache
        }
        class ProfessorUseCaseImpl {
            -ProfessorRepository professorRepository
            -List~Professor~ professorCache
        }
    }

    namespace Dominio_Puertos {
        class EnrollmentUseCase { <<interface>> }
        class StudentUseCase { <<interface>> }
        class CourseUseCase { <<interface>> }
        class ProfessorUseCase { <<interface>> }
        class EnrollmentQueueRepository { <<interface>> }
        class StudentRepository { <<interface>> }
        class CourseRepository { <<interface>> }
        class ProfessorRepository { <<interface>> }
    }

    namespace Compartido {
        class AVLTree~T~
        class SortingAlgorithms { <<utility>> }
        class SearchAlgorithms { <<utility>> }
    }

    EnrollmentUseCaseImpl ..|> EnrollmentUseCase : implementa
    StudentUseCaseImpl ..|> StudentUseCase : implementa
    CourseUseCaseImpl ..|> CourseUseCase : implementa
    ProfessorUseCaseImpl ..|> ProfessorUseCase : implementa

    EnrollmentUseCaseImpl --> EnrollmentQueueRepository : depende de
    EnrollmentUseCaseImpl --> CourseRepository : depende de
    EnrollmentUseCaseImpl --> StudentRepository : depende de
    StudentUseCaseImpl --> StudentRepository : depende de
    CourseUseCaseImpl --> CourseRepository : depende de
    ProfessorUseCaseImpl --> ProfessorRepository : depende de

    EnrollmentUseCaseImpl --> SortingAlgorithms : usa
    StudentUseCaseImpl --> AVLTree : usa
    CourseUseCaseImpl --> SearchAlgorithms : usa binarySearchByKey
    ProfessorUseCaseImpl --> SearchAlgorithms : usa binarySearch
```

> Este es el diagrama que mejor evidencia el rol "orquestador" de la aplicación: implementa puertos de entrada (hacia el dominio), inyecta puertos de salida (también del dominio, aunque implementados en infraestructura) y consume las estructuras/algoritmos de la capa compartida.

---

### 2.3 Infraestructura ↔ Dominio

```mermaid
classDiagram
    direction LR

    namespace Infraestructura_Persistencia {
        class EnrollmentQueueRepositoryImpl {
            -DatabaseConnection db
        }
        class StudentRepositoryImpl {
            -DatabaseConnection db
        }
        class DatabaseConnection {
            <<Singleton>>
        }
    }

    namespace Infraestructura_UI {
        class MainConsole
        class StudentConsole
        class AdminConsole
    }

    namespace Dominio_Puertos {
        class EnrollmentQueueRepository { <<interface>> }
        class StudentRepository { <<interface>> }
        class StudentUseCase { <<interface>> }
        class EnrollmentUseCase { <<interface>> }
    }

    EnrollmentQueueRepositoryImpl ..|> EnrollmentQueueRepository : implementa
    StudentRepositoryImpl ..|> StudentRepository : implementa
    EnrollmentQueueRepositoryImpl --> DatabaseConnection : usa
    StudentRepositoryImpl --> DatabaseConnection : usa

    StudentConsole --> StudentUseCase : depende de
    StudentConsole --> EnrollmentUseCase : depende de
    MainConsole --> StudentConsole : orquesta
    MainConsole --> AdminConsole : orquesta
```

> Aquí se ve la doble cara de la infraestructura: el adaptador de persistencia **implementa** puertos de salida del dominio, mientras que el adaptador de consola **consume** puertos de entrada del dominio — nunca al revés, y nunca conociéndose entre sí (`StudentConsole` no conoce `StudentRepositoryImpl`).

---

### 2.4 Compartido ↔ Aplicación/Dominio

```mermaid
classDiagram
    direction LR

    namespace Compartido {
        class AVLTree~T~ {
            +insert(T) void
            +search(String) T
        }
        class SortingAlgorithms { <<utility>> }
        class SearchAlgorithms { <<utility>> }
    }

    namespace Aplicacion {
        class StudentUseCaseImpl
        class EnrollmentUseCaseImpl
        class CourseUseCaseImpl
        class ProfessorUseCaseImpl
    }

    namespace Dominio_Modelo {
        class Student
        class EnrollmentQueueEntry
    }

    StudentUseCaseImpl --> AVLTree : usa como índice
    AVLTree ..> Student : indexa

    EnrollmentUseCaseImpl --> SortingAlgorithms : usa para priorizar cola
    SortingAlgorithms ..> EnrollmentQueueEntry : ordena

    CourseUseCaseImpl --> SearchAlgorithms : usa binarySearchByKey
    ProfessorUseCaseImpl --> SearchAlgorithms : usa binarySearch
```

> Lo relevante de este diagrama: `shared` no depende de nadie (ni dominio ni aplicación), pero sí es referenciado por ambos — su rol es puramente utilitario y transversal, coherente con el hecho de que puede reutilizarse en cualquier capa.

---

## Parte 3: Vista global reducida

Diagrama de portada del capítulo: una selección de clases representativas por cada capa, mostrando el flujo completo de dependencias del sistema (UI → puertos de entrada → aplicación → puertos de salida → persistencia), más el punto de apoyo transversal de `shared`.

```mermaid
classDiagram
    direction LR

    namespace Infraestructura_UI {
        class StudentConsole
    }

    namespace Dominio_Puertos_Entrada {
        class EnrollmentUseCase { <<interface>> }
        class StudentUseCase { <<interface>> }
    }

    namespace Capa_Aplicacion {
        class EnrollmentUseCaseImpl
        class StudentUseCaseImpl
    }

    namespace Dominio_Puertos_Salida {
        class EnrollmentQueueRepository { <<interface>> }
        class StudentRepository { <<interface>> }
    }

    namespace Infraestructura_Persistencia {
        class EnrollmentQueueRepositoryImpl
        class StudentRepositoryImpl
        class DatabaseConnection { <<Singleton>> }
    }

    namespace Compartido {
        class AVLTree~T~
        class SortingAlgorithms { <<utility>> }
    }

    namespace Dominio_Modelo {
        class Student
        class EnrollmentQueueEntry
    }

    StudentConsole --> StudentUseCase : consume
    StudentConsole --> EnrollmentUseCase : consume

    EnrollmentUseCaseImpl ..|> EnrollmentUseCase : implementa
    StudentUseCaseImpl ..|> StudentUseCase : implementa

    EnrollmentUseCaseImpl --> EnrollmentQueueRepository : depende de
    StudentUseCaseImpl --> StudentRepository : depende de

    EnrollmentQueueRepositoryImpl ..|> EnrollmentQueueRepository : implementa
    StudentRepositoryImpl ..|> StudentRepository : implementa
    EnrollmentQueueRepositoryImpl --> DatabaseConnection : usa
    StudentRepositoryImpl --> DatabaseConnection : usa

    EnrollmentUseCaseImpl --> SortingAlgorithms : usa
    StudentUseCaseImpl --> AVLTree : usa

    AVLTree ..> Student : indexa
    SortingAlgorithms ..> EnrollmentQueueEntry : ordena
```

> **Lectura del diagrama:** siguiendo el flujo de izquierda a derecha se observa el ciclo completo de una petición: la consola consume un puerto de entrada, la implementación de aplicación satisface ese puerto y a la vez depende de un puerto de salida (abstracción), el cual es finalmente implementado por el adaptador de persistencia que usa la conexión Singleton. En paralelo, la capa `shared` se apoya transversalmente sobre la aplicación para indexar (`AVLTree`) y ordenar (`SortingAlgorithms`) entidades del dominio, sin depender de ninguna otra capa. Esta es la síntesis visual del Principio de Inversión de Dependencias aplicado en el proyecto.

---

## Índice de diagramas

| # | Diagrama | Tipo |
|---|----------|------|
| 1.1 | Capa de Dominio | Aislado |
| 1.2 | Capa de Aplicación | Aislado |
| 1.3 | Capa de Infraestructura | Aislado |
| 1.4 | Capa Compartida | Aislado |
| 2.1 | Dominio ↔ Aplicación/Infraestructura | Con relaciones cruzadas |
| 2.2 | Aplicación ↔ Dominio/Compartido | Con relaciones cruzadas |
| 2.3 | Infraestructura ↔ Dominio | Con relaciones cruzadas |
| 2.4 | Compartido ↔ Aplicación/Dominio | Con relaciones cruzadas |
| 3 | Vista global reducida | Síntesis general |
