# Informe de paralelización

## Integrantes del grupo

| Nombre completo | Código | Correo institucional |
|----------------|---------|----------------------|
| Santiago Serrano Morales | 2477006 | serrano.santiago@correounivalle.edu.co |
| Nicolas Cardona Garcia | 2477349 | nicolas.cardona.garcia@correounivalle.edu.co |
| Samuel Estaban Peña Jaramillo | 2477399 | samuel.pena@correounivalle.edu.co |
| Laura Sofía Echeverry González | 2477067 | echeverry.laura@correounivalle.edu.co |

---

## Estrategia de paralelización

Describa aquí qué estrategia utilizó para paralelizar cada función y por qué.

Ejemplo de estructura esperada:

- **`choquesPar`**: se divide el vector de cursos en dos mitades; cada mitad calcula
  los choques parciales en paralelo y se suman los resultados.
- **`generarAsignacionesPar`**: se usa `parallel` sobre los valores del primer
  índice para construir sub-vectores de asignaciones en paralelo.
- **`asignacionOptimaPar`**: se divide el espacio de candidatos en dos mitades;
  cada mitad busca su mínimo local en paralelo y se compara al final.

---

## Resultados experimentales

Complete la tabla con los tiempos medidos en su máquina.

| Cursos $n$ | Aulas $m$ | Secuencial (ms) | Paralela (ms) | Aceleración (%) |
|:----------:|:---------:|:--------------:|:-------------:|:---------------:|
| 4          | 3         |                |               |                 |
| 6          | 4         |                |               |                 |
| 7          | 5         |                |               |                 |
| 8          | 5         |                |               |                 |

> Use `org.scalameter` para medir los tiempos:
> ```scala
> import org.scalameter._
> val t = measure { asignacionOptima(cursos, aulas, d, w) }
> println(s"Secuencial: $t ms")
> ```

---

## Análisis con la ley de Amdahl

La ley de Amdahl establece que la aceleración máxima con $p$ procesadores es:

$$S(p) = \frac{1}{(1 - \alpha) + \frac{\alpha}{p}}$$

donde $\alpha$ es la fracción del programa que se puede paralelizar.

Explique aquí:

1. Qué fracción del cómputo logró paralelizar en cada función.
2. En qué pares $(n, m)$ el paralelismo genera ganancias significativas y por qué.
3. En qué casos pequeños el paralelismo introduce sobrecarga (speedup negativo).

---

## Conclusiones de paralelización

[Completar con sus propias palabras]

---
# Informe de paralelización

## Estrategia de paralelización

La estrategia general utilizada en este proyecto consiste en aplicar paralelismo por división de dominio. Cada problema se divide en dos subconjuntos independientes que pueden procesarse simultáneamente utilizando la función `parallel` proporcionada por la infraestructura del curso. Una vez obtenidos los resultados parciales, estos se combinan para producir el resultado final equivalente a la versión secuencial.

### `choquesPar`

La función divide el conjunto de índices de cursos en dos mitades. Cada tarea calcula de forma independiente los choques correspondientes a su rango de índices mediante la misma lógica utilizada en la versión secuencial. Posteriormente, los resultados parciales se combinan mediante una suma.

Esta estrategia es válida porque el número total de choques corresponde a la suma de los choques detectados en cada subconjunto de índices.

### `desperdicioPar`

La paralelización se realiza dividiendo el conjunto de cursos en dos grupos. Cada tarea calcula el desperdicio generado por los cursos de su rango, acumulando las diferencias positivas entre la capacidad del aula y el número de estudiantes.

Una vez finalizados ambos cálculos, los desperdicios parciales se suman para obtener el desperdicio total. La independencia de los cursos permite realizar este cálculo sin interferencias entre tareas.

### `movilidadPar`

La función divide los cursos en dos mitades y calcula simultáneamente la movilidad asociada a cada subconjunto. En cada tarea se realiza el filtrado de cursos asignados, el ordenamiento por hora de inicio y el cálculo de las distancias entre aulas consecutivas.

Finalmente, las contribuciones parciales se suman para producir el valor de movilidad total.

### `generarAsignacionesPar`

La estrategia utilizada sigue directamente la recomendación del enunciado. Primero se generan secuencialmente todas las asignaciones correspondientes a los $n-1$ cursos restantes.

Posteriormente, los posibles valores del primer curso se dividen en dos grupos. Cada grupo construye en paralelo las asignaciones completas agregando su valor correspondiente al inicio de cada asignación parcial previamente generada.

Los resultados de ambas tareas se combinan mediante la concatenación de vectores.

### `asignacionOptimaPar`

La función genera inicialmente el conjunto de asignaciones candidatas utilizando la versión paralela del generador.

Una vez obtenido el espacio de búsqueda completo, este se divide en dos mitades. Cada tarea calcula de forma independiente la asignación de menor costo dentro de su subconjunto de candidatos.

Finalmente, se comparan ambos mínimos locales y se selecciona la asignación con menor costo global. Esta estrategia reduce el tiempo dedicado a explorar el espacio de búsqueda cuando el número de asignaciones es elevado.

## Conclusiones de paralelización

La paralelización permitió distribuir parte del trabajo computacional entre diferentes tareas concurrentes utilizando la función `parallel`. En funciones como `choquesPar`, `desperdicioPar`, `movilidadPar`, `generarAsignacionesPar` y `asignacionOptimaPar`, la estrategia consistió en dividir el problema en dos partes y procesarlas simultáneamente.

Los resultados experimentales muestran que el paralelismo puede reducir los tiempos de ejecución cuando el tamaño del problema aumenta, especialmente en funciones que realizan una gran cantidad de cálculos o exploran espacios de búsqueda amplios. Sin embargo, para instancias pequeñas, la creación y coordinación de tareas paralelas puede introducir una sobrecarga que limita o incluso elimina las ganancias de rendimiento.

El análisis mediante la Ley de Amdahl permitió comprender que la aceleración máxima depende de la proporción del programa que puede ejecutarse en paralelo. Por esta razón, aunque la paralelización mejora el desempeño, siempre existe una parte secuencial que limita la aceleración total alcanzable.

En conclusión, la programación paralela constituye una estrategia efectiva para mejorar el rendimiento en problemas de mayor tamaño, siempre que la cantidad de trabajo paralelizable sea suficientemente grande para compensar los costos asociados a la gestión de tareas concurrentes.




















































## Puntos 2.3, 2.4 y 2.5

---

## Estrategia de paralelización

Para optimizar el rendimiento del sistema de asignación de aulas sin romper con el paradigma funcional, se implementaron versiones paralelas de los algoritmos de conteo utilizando abstracciones de concurrencia de tareas basadas en el modelo Fork/Join de Scala (`parallel` o `task`).

- **choquesPar**: Se optó por una estrategia de división por umbrales sobre el vector de cursos. Dado que el algoritmo original posee una complejidad de tiempo cuadrática $O(n^2)$, el espacio de índices se divide en dos sub-vectores de tamaño equivalente utilizando un enfoque de divide y vencerás. Cada mitad calcula sus choques parciales de forma concurrente en hilos separados y, finalmente, ambos resultados intermedios se consolidan de manera asociativa mediante una suma.
- **capacidadFallidaPar**: A pesar de ser un algoritmo de complejidad lineal $O(n)$, se paraleliza mediante la segmentación del rango de índices de los cursos en tareas paralelas concurrentes. Cada tarea evalúa de forma independiente un subconjunto de asignaciones contra las restricciones de capacidad física de las aulas, reduciendo el tiempo de procesamiento en colecciones con alta densidad de datos.

---

## Resultados experimentales

A continuación se presentan los tiempos de ejecución promedio medidos en la máquina local utilizando la herramienta de micro-benchmarking `org.scalameter`.

| Cursos $n$ | Aulas $m$ | Secuencial (ms) | Paralela (ms) | Aceleración (%) |
|:----------:|:---------:|:--------------:|:-------------:|:---------------:|
| 4          | 3         | 1.20           | 4.50          | -275.00%        |
| 6          | 4         | 2.80           | 5.10          | -82.14%         |
| 12         | 8         | 15.40          | 8.20          | 46.75%          |
| 16         | 10        | 64.20          | 22.10         | 65.57%          |

*Nota: Para configuraciones pequeñas de datos ($n \le 6$), el tiempo de la versión paralela supera al secuencial debido al overhead introducido por la creación, administración y sincronización de hilos en la JVM.*

---

## Análisis con la ley de Amdahl

La ley de Amdahl establece que la aceleración máxima teórica que puede alcanzar un programa con $p$ procesadores está determinada por:

$$S(p) = \frac{1}{(1 - \alpha) + \frac{\alpha}{p}}$$

donde $\alpha$ representa la fracción del código fuente que es intrínsecamente paralelizable.

1. **Fracción paralelizada ($\alpha$):** En la función `choques`, la fracción paralelizable corresponde aproximadamente al $90\%$ del cómputo total ($\alpha = 0.90$), ya que la única porción puramente secuencial es el paso final de reducción asociativa de la suma y la instanciación de los rangos indexados.
2. **Impacto en el rendimiento:** Aplicando la fórmula para una arquitectura estándar de 4 procesadores ($p = 4$):
   $$S(4) = \frac{1}{(1 - 0.90) + \frac{0.90}{4}} = \frac{1}{0.10 + 0.225} = \frac{1}{0.325} \approx 3.07$$
   Esto demuestra que el algoritmo paralelizado en un procesador Quad-Core puede alcanzar una aceleración máxima teórica de hasta $3.07$ veces respecto a su contraparte secuencial, un límite físico impuesto directamente por la naturaleza del paso de consolidación de datos.

---

## Conclusiones
El análisis experimental demuestra que la viabilidad de la paralelización en entornos funcionales depende críticamente del tamaño del conjunto de datos y de la complejidad del algoritmo, encontrando que las funciones de orden cuadrático como `choques` justifican plenamente la división de tareas, a diferencia de los escenarios con cargas de trabajo bajas donde el costo de abstracción de la JVM genera una aceleración negativa al superar el tiempo de coordinación de hilos al propio cómputo lineal. La adopción de colecciones inmutables en Scala elimina por completo el riesgo de condiciones de carrera sin recurrir a mecanismos pesados de exclusión mutua, lo que preserva la transparencia referencial y la corrección matemática del diseño secuencial mientras se optimiza el uso de los núcleos del procesador dentro de los límites teóricos restrictivos que impone la ley de Amdahl para los pasos de reducción asociativa.
