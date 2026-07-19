-- Datos de prueba (seeder) para el sistema de Matricula UTP
-- Ejecutar sobre la base "matricula" (la usada por la app, ver docker-compose.yaml):
--   docker exec -i postgres_container psql -U admin -d matricula -f /ruta/a/seed.sql
-- o copiando el archivo dentro del contenedor con docker cp.

BEGIN;

-- ============ PROFESORES (10) ============
INSERT INTO professors (code, name, lastname, email) VALUES
    ('P00000001', 'Carlos', 'Ramirez Soto', 'carlos.ramirez@utp.edu.pe'),
    ('P00000002', 'Maria', 'Fernandez Lopez', 'maria.fernandez@utp.edu.pe'),
    ('P00000003', 'Jorge', 'Quispe Mamani', 'jorge.quispe@utp.edu.pe'),
    ('P00000004', 'Ana', 'Torres Vega', 'ana.torres@utp.edu.pe'),
    ('P00000005', 'Luis', 'Huaman Rojas', 'luis.huaman@utp.edu.pe'),
    ('P00000006', 'Patricia', 'Salazar Diaz', 'patricia.salazar@utp.edu.pe'),
    ('P00000007', 'Roberto', 'Castillo Pena', 'roberto.castillo@utp.edu.pe'),
    ('P00000008', 'Gabriela', 'Mendoza Rios', 'gabriela.mendoza@utp.edu.pe'),
    ('P00000009', 'Fernando', 'Chavez Paredes', 'fernando.chavez@utp.edu.pe'),
    ('P00000010', 'Silvia', 'Guerrero Nunez', 'silvia.guerrero@utp.edu.pe')
ON CONFLICT (code) DO NOTHING;

-- ============ CURSOS (10) ============
INSERT INTO courses (code, name, max_students, enrollment_open) VALUES
    ('PROG101', 'Programacion I', 30, TRUE),
    ('PROG201', 'Programacion II', 30, TRUE),
    ('BD101', 'Base de Datos I', 25, TRUE),
    ('ALGO101', 'Algoritmos y Estructuras de Datos', 30, TRUE),
    ('MAT101', 'Matematica Discreta', 35, FALSE),
    ('RED101', 'Redes de Computadoras', 25, TRUE),
    ('ING101', 'Ingenieria de Software', 28, TRUE),
    ('SO101', 'Sistemas Operativos', 25, FALSE),
    ('WEB101', 'Desarrollo Web', 30, TRUE),
    ('IA101', 'Inteligencia Artificial', 20, TRUE)
ON CONFLICT (code) DO NOTHING;

-- ============ ALUMNOS (15) ============
INSERT INTO students (code, name, lastname, email, gpa) VALUES
    ('A00000001', 'Diego', 'Ramos Flores', 'diego.ramos@utp.edu.pe', 15.75),
    ('A00000002', 'Valeria', 'Cruz Espinoza', 'valeria.cruz@utp.edu.pe', 17.20),
    ('A00000003', 'Sebastian', 'Paredes Ortiz', 'sebastian.paredes@utp.edu.pe', 13.40),
    ('A00000004', 'Camila', 'Rojas Medina', 'camila.rojas@utp.edu.pe', 16.80),
    ('A00000005', 'Mateo', 'Vargas Silva', 'mateo.vargas@utp.edu.pe', 12.90),
    ('A00000006', 'Lucia', 'Herrera Campos', 'lucia.herrera@utp.edu.pe', 18.10),
    ('A00000007', 'Andres', 'Flores Quiroz', 'andres.flores@utp.edu.pe', 14.60),
    ('A00000008', 'Sofia', 'Aguirre Bravo', 'sofia.aguirre@utp.edu.pe', 16.30),
    ('A00000009', 'Gabriel', 'Nunez Solano', 'gabriel.nunez@utp.edu.pe', 11.75),
    ('A00000010', 'Isabella', 'Rios Delgado', 'isabella.rios@utp.edu.pe', 17.90),
    ('A00000011', 'Nicolas', 'Vega Castro', 'nicolas.vega@utp.edu.pe', 13.20),
    ('A00000012', 'Renata', 'Ibanez Torres', 'renata.ibanez@utp.edu.pe', 15.00),
    ('A00000013', 'Emilio', 'Chirinos Palma', 'emilio.chirinos@utp.edu.pe', 12.40),
    ('A00000014', 'Antonella', 'Guevara Luna', 'antonella.guevara@utp.edu.pe', 16.55),
    ('A00000015', 'Joaquin', 'Bustamante Rios', 'joaquin.bustamante@utp.edu.pe', 14.85)
ON CONFLICT (code) DO NOTHING;

-- Sincroniza los contadores de secuencia para que generateNextCode()
-- siga generando codigos A00000016 / P00000011 en adelante sin choques.
UPDATE sequence_counters SET last_value = GREATEST(last_value, 15) WHERE entity = 'student';
UPDATE sequence_counters SET last_value = GREATEST(last_value, 10) WHERE entity = 'professor';

COMMIT;
