# Conclusiones

## Integrantes del grupo

| Nombre completo | Código | Correo institucional |
|----------------|---------|----------------------|
| Santiago Serrano Morales | 2477006 | serrano.santiago@correounivalle.edu.co |
| Nicolas Cardona Garcia | 2477349 | nicolas.cardona.garcia@correounivalle.edu.co |
| Samuel Estaban Peña Jaramillo | 2477399 | samuel.pena@correounivalle.edu.co |
| Laura Sofía Echeverry González | 2477067 | echeverry.laura@correounivalle.edu.co |

---

## Conclusiones del proyecto

Presente aquí las conclusiones del proyecto. Como mínimo debe responder:

1. **Programación funcional:** ¿Qué ventajas y dificultades encontraron al implementar
   la solución usando recursión y funciones de alto orden en lugar de ciclos iterativos?

2. **Corrección:** ¿Cómo argumentaron formalmente que sus implementaciones son correctas?
   ¿Qué técnicas de inducción estructural o de invariantes aplicaron?

3. **Paralelismo:** ¿En qué escenarios resultó beneficioso paralelizar? ¿Cuándo la
   sobrecarga del sistema superó la ganancia esperada?

4. **Aprendizajes:** ¿Qué conceptos del curso les resultaron más útiles para resolver
   el problema? ¿Qué cambiarían en su diseño si volvieran a empezar?

---

## Conclusiones del proyecto

### 1. Programación funcional

La programación funcional permitió desarrollar soluciones más modulares y expresivas mediante el uso de funciones de alto orden como `map`, `filter`, `sum` y `sortBy`, reduciendo la necesidad de variables mutables y ciclos iterativos. Sin embargo, una de las principales dificultades fue adaptar algunos algoritmos a un estilo recursivo y funcional, especialmente en problemas donde la solución iterativa resulta más intuitiva.

### 2. Corrección

La correctitud de las implementaciones se argumentó formalmente utilizando los métodos estudiados en el curso. Para las funciones recursivas se empleó inducción estructural sobre la entrada, verificando el caso base y el paso inductivo. Para los algoritmos iterativos se definieron estados, transformaciones e invariantes que permitieron demostrar que el resultado obtenido coincide con la especificación matemática de cada función.

### 3. Paralelismo

La paralelización resultó beneficiosa en funciones que procesan grandes cantidades de datos o exploran espacios de búsqueda amplios, como la generación de asignaciones y la búsqueda de la asignación óptima. En instancias pequeñas, la creación y sincronización de tareas paralelas introduce una sobrecarga que puede reducir o incluso eliminar las ganancias de rendimiento, tal como predice la Ley de Amdahl.

### 4. Aprendizajes

Los conceptos más útiles para resolver el proyecto fueron la programación funcional, la recursión, las funciones de alto orden, la argumentación formal de correctitud y las estrategias de paralelización. Si se desarrollara nuevamente el proyecto, se buscaría diseñar desde el inicio una arquitectura más orientada a la reutilización de código y a la evaluación temprana del rendimiento de las versiones paralelas para optimizar su eficiencia.
