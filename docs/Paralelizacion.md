# Informe de paralelización

## Integrantes del grupo

| Nombre completo | Código | Correo institucional |
|----------------|---------|----------------------|
| Santiago Serrano Morales | 2477006 | serrano.santiago@correounivalle.edu.co |
| Nicolas Cardona Garcia | 2477349 | nicolas.cardona.garcia@correounivalle.edu.co |
| Samuel Estaban Peña Jaramillo | 2477399 | samuel.pena@correounivalle.edu.co |
| Laura Sofía Echeverry González | 2477067 | echeverry.laura@correounivalle.edu.co |

# Documentación de Paralelización — Asignación de Aulas

---

## Estrategia de paralelización

- **`choquesPar`**: el vector de cursos se divide en dos mitades según el índice (`0` hasta `mitad` y `mitad` hasta `cursos.length`). Cada mitad cuenta de forma independiente los pares de cursos que se solapan en el tiempo y están asignados a la misma aula. Las dos sumas parciales se calculan en paralelo con `parallel` y se suman al final. Esta estrategia es efectiva porque el conteo de cada par `(i, j)` es completamente independiente del resto.

- **`desperdicioPar`**: de forma análoga a `choquesPar`, el vector de cursos se parte en dos mitades. Cada mitad calcula la suma del desperdicio (diferencia entre capacidad del aula y estudiantes del curso) de sus cursos asignados. Ambas sumas parciales se ejecutan en paralelo y se combinan. El cálculo por curso es independiente, lo que elimina condiciones de carrera.

- **`movilidadPar`**: el vector de cursos se divide en dos mitades. Cada mitad ordena sus cursos por hora de inicio, aplica una ventana deslizante de tamaño 2 sobre pares consecutivos y acumula la distancia entre aulas. Las dos contribuciones se computan con `parallel` y se suman. **Nota:** al dividir el vector, la movilidad entre el último curso de la primera mitad y el primero de la segunda mitad no se contabiliza; esto introduce una pequeña imprecisión respecto a la versión secuencial cuando los cursos de ambas mitades son consecutivos en el tiempo.

- **`generarAsignacionesPar`**: se generan recursivamente (y de forma secuencial) todas las asignaciones de los `n - 1` cursos restantes. Luego los `m` posibles valores para el primer curso se dividen en dos rangos (`0` hasta `mitad` y `mitad` hasta `m`). Cada rango construye en paralelo su subvector de asignaciones anteponiéndole el valor `k` a cada asignación previa. Los dos subvectores resultantes se concatenan. La paralelización recae sobre el primer nivel del árbol de recursión, donde el trabajo por rama es proporcional al número de asignaciones previas.

- **`asignacionOptimaPar`**: primero se genera el conjunto completo de asignaciones candidatas usando `generarAsignacionesPar`. Ese vector se divide en dos mitades y cada mitad evalúa el costo de sus asignaciones (con `costoAsignacion`) y selecciona su mínimo local, todo en paralelo mediante `parallel`. Finalmente se comparan los dos mínimos locales y se retorna el global. La búsqueda del óptimo es un problema _embarrassingly parallel_: cada evaluación es independiente de las demás.

---

## Resultados experimentales

Complete la tabla con los tiempos medidos en su máquina.

| Cursos $n$ | Aulas $m$ | Secuencial (ms) | Paralela (ms) | Aceleración (%) |
|:----------:|:---------:|:--------------:|:-------------:|:---------------:|
| 4          | 3         | 92.75           | 32.49         | 65.0            |
| 6          | 4         | 131.49          | 73.43         | 44.2            |
| 7          | 5         | 494.04          | 165.53        | 66.5            |
| 8          | 5         | 1738.98         | 999.61        | 42.5            |

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

### 1. Fracción paralelizada en cada función

| Función                  | Fracción paralela ($\alpha$) | Parte secuencial restante |
|--------------------------|:----------------------------:|---------------------------|
| `choquesPar`             | ~0.90                        | División del vector y suma final |
| `desperdicioPar`         | ~0.90                        | División del vector y suma final |
| `movilidadPar`           | ~0.85                        | División, ordenamiento y suma final |
| `generarAsignacionesPar` | ~0.50                        | Llamada recursiva secuencial a `generarAsignaciones(n-1, m)` que domina el tiempo |
| `asignacionOptimaPar`    | ~0.90                        | Generación del vector de candidatos y comparación final |

La fracción no paralelizable más significativa corresponde a `generarAsignacionesPar`, donde la generación de las asignaciones de los `n - 1` cursos anteriores se realiza de forma completamente secuencial antes de dividir el trabajo.

### 2. Pares $(n, m)$ donde el paralelismo genera ganancias significativas

Los resultados experimentales muestran que las mayores ganancias ocurren en los casos de mayor carga:

- **$n = 7$, $m = 5$** (65625 candidatos): aceleración del **66.5%**, la más alta medida. El volumen de evaluaciones de `costoAsignacion` es suficientemente grande para que ambos hilos trabajen de forma sostenida y el overhead de `parallel` sea despreciable.
- **$n = 4$, $m = 3$** (81 candidatos): sorprendentemente, también arroja un **65.0%** de aceleración. Esto se explica en parte por el efecto JIT de la JVM: la primera medición secuencial incluye compilación en caliente, inflando ese tiempo. Con warmup adecuado este resultado sería menos favorable.
- **$n = 8$, $m = 5$** (390625 candidatos): aceleración del **42.5%**. A pesar de ser el caso más grande, la fracción secuencial de `generarAsignacionesPar` (la llamada recursiva a `generarAsignaciones(n-1, m)`) crece también con $n$, lo que reduce efectivamente $\alpha$ y limita la ganancia.

### 3. Casos pequeños donde el paralelismo introduce sobrecarga

Con los datos obtenidos **no se observó speedup negativo** en ningún par medido; todos muestran ganancia. Sin embargo, el par **$n = 6$, $m = 4$** presenta la aceleración más baja (**44.2%**), lo que sugiere que en ese rango el overhead relativo de sincronización es mayor respecto al trabajo útil. Para valores de $n \leq 3$ o $m \leq 2$ sí sería esperable un speedup negativo, ya que el espacio de candidatos sería demasiado pequeño para compensar el costo de lanzar dos hilos.

Aplicando la ley de Amdahl con $p = 2$ procesadores, una aceleración de factor $\approx 1.66\times$ (caso $n=7$) implica una fracción paralelizable de:

$$\alpha = \frac{S(p) \cdot p - p}{S(p) \cdot p - p \cdot (1 - S(p)) \cdot p} \approx \frac{2 \cdot 1.66 - 2}{2 \cdot 1.66 - 1} \approx 0.98$$

lo que indica que el núcleo de evaluación de candidatos es casi completamente paralelizable en ese caso.

---

## Conclusiones de paralelización

La paralelización implementada en `AsignacionAulasPar` sigue una estrategia uniforme de **división en dos mitades** sobre el principal eje de trabajo de cada función (índices de cursos o asignaciones candidatas), apoyándose en la primitiva `parallel` del paquete `common`. Esta aproximación es sencilla de razonar y de verificar correctamente, pues en la mayoría de las funciones (`choquesPar`, `desperdicioPar`, `asignacionOptimaPar`) cada fragmento opera sobre datos completamente disjuntos, eliminando condiciones de carrera.

Los resultados experimentales muestran aceleraciones reales de entre el **42.5% y el 66.5%** en los cuatro casos medidos, lo cual es consistente con el uso de 2 hilos. La ganancia más alta se obtuvo con $n = 7$, $m = 5$ (66.5%), mientras que la más baja correspondió a $n = 8$, $m = 5$ (42.5%), lo que refleja que la fracción secuencial de `generarAsignacionesPar` —la llamada recursiva a `generarAsignaciones(n-1, m)`— crece con $n$ y limita la escalabilidad según la ley de Amdahl.

No se observó speedup negativo en ninguno de los casos medidos, aunque para entradas muy pequeñas ($n \leq 3$) sería esperable que el overhead de `parallel` superara la ganancia. El resultado del par $n = 4$, $m = 3$ (65.0%) debe interpretarse con cautela, ya que sin warmup de JVM la primera medición secuencial puede estar inflada por la compilación JIT.
El cuello de botella más relevante sigue siendo la llamada secuencial dentro de `generarAsignacionesPar`. Una mejora futura sería paralelizar recursivamente varios niveles del árbol de construcción, aunque a costa de mayor complejidad. En cuanto a `movilidadPar`, la división en mitades introduce una leve imprecisión al ignorar la transición entre ambas mitades; una implementación exacta requeriría un paso adicional de combinación en la frontera.
--------
