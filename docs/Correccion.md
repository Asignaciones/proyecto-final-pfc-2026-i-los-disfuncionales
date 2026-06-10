# Informe de corrección

## Puntos 2.3, 2.4 y 2.5

## Argumentación de corrección de programas

### Argumentando sobre corrección de programas recursivos

#### 1. Función `solapan`

```scala
/** Devuelve true si los intervalos [ini1, fin1) y [ini2, fin2) se traslapan. */
def solapan(c1: Curso, c2: Curso): Boolean =
iniCurso(c1) < finCurso(c2) && iniCurso(c2) < finCurso(c1)
```
Para demostrar la corrección del predicado temporal de orden constante, aplicamos lógica proposicional directa sobre sus restricciones de dominio.

Sea la especificación matemática del traslape entre dos intervalos de tiempo abiertos por la derecha:

$$\forall c_1, c_2 \in Cursos : f_{solapan}(c_1, c_2) \equiv (ini_1 < fin_2 \land ini_2 < fin_1)$$

Dado que la función en Scala no genera bifurcaciones complejas, ciclos ni llamadas recursivas, su corrección estructural es inmediata por identidad lógica con la especificación. Al evaluar las condiciones simultáneamente, devuelve verdadero si y solo si los intervalos intersecan en la línea de tiempo, garantizando transparencia referencial absoluta.

---

### Argumentando sobre corrección de programas iterativos

Para evaluar funciones basadas en transformaciones y filtrado de colecciones indexadas, modelamos el comportamiento de los combinadores de alto orden de Scala (`.map`, `.count`, `.sum`) como programas iterativos genéricos basados en transiciones de estados estructurados.

#### 2. Función choques

```scala
/**
   * Número de pares (i, j) con i < j tales que a(i) == a(j) >= 0
   * y los cursos i y j se solapan.
   */
  def choques(cursos: Cursos, a: Asignacion): Int = {
    cursos.indices.map { i =>
      // Para cada curso i contamos los choques con los cursos posteriores
      cursos.indices.drop(i + 1).count { j =>
        // Hay choque si ambos cursos están en la misma aula,
        // el aula es válida y los horarios se traslapan
        a(i) >= 0 &&
          a(i) == a(j) &&
          solapan(cursos(i), cursos(j))
      }
    }.sum // Sumamos todos los choques encontrados
  }
```
La función realiza un cálculo combinatorio sobre los pares de índices $(i, j)$ tales que $i < j$. Podemos descomponer su comportamiento en dos estados iterativos acoplados: un bucle externo controlado por el índice $i$ y un bucle interno perezoso provocado por el método `.drop(i + 1).count`.

##### Modelado de Estados del Procesamiento:
- **Estado Externo:** Cuyo progreso se define por la terna $(i, n, ac_{total})$, donde $ac_{total}$ acumula los choques de todos los elementos previos evaluados.
- **Estado Inicial Externo:** $s_0 = (0, n, 0)$.
- **Estado Final Externo:** Cuando $i = n$.
- **Invariante Externo:** $$\text{Inv}_{ext}(i, n, ac_{total}) \equiv ac_{total} = \sum_{k=0}^{i-1} \text{cuenta\_parcial}(k)$$

##### Demostración analítica:
- **Base:** Cuando $i = 0$, la suma no contiene términos, por lo que $ac_{total} = 0$, satisfaciendo el estado inicial.
- **Paso inductivo:** Al pasar de $i$ a $i+1$, el método `.map` invoca la sub-evaluación interna sobre el rango restante recortado por `.drop(i + 1)`. Esto garantiza que el índice $j$ siempre sea estrictamente mayor que $i$ ($i < j$), cumpliendo la restricción matemática del problema. El conteo parcial generado se añade asociativamente a través de `.sum`. Cuando $i = n$, la reducción final entrega la suma total exacta de colisiones sin duplicaciones.

---

#### 3. Función capacidadFallida

```scala
/** Cantidad de cursos cuya aula asignada tiene capacidad menor al número de estudiantes. */
  def capacidadFallida(cursos: Cursos, aulas: Aulas, a: Asignacion): Int =
    cursos.indices          // genera los índices 0..n-1
      .count { i =>         // cuenta directamente los que cumplan la condición
        a(i) >= 0 &&        // el curso está asignado a algún aula
          capAula(aulas(a(i))) < estCurso(cursos(i))  // el aula no alcanza para los estudiantes
      }
```
##### Modelado de Estados para capacidadFallida:
- **Estado $s$:** Terna de la forma $(i, n, ac)$, donde $i$ es el índice del curso actual bajo evaluación, $n$ es la cantidad total de cursos, y $ac$ es el acumulador entero con la cuenta de violaciones de capacidad detectadas.
- **Estado inicial $s_0$:** $(0, n, 0)$.
- **Estado final $s_f$:** Condición de parada cuando $i = n$.
- **Transformación de estados:**
  $$(i, n, ac) \to (i + 1, n, ac + \text{evaluar}(i))$$
  Donde el predicado de evaluación se define como:
  $$\text{evaluar}(i) = \begin{cases} 1 & \text{si } a(i) \ge 0 \land cap(aula(a(i))) < est(curso(i)) \\ 0 & \text{en caso contrario} \end{cases}$$

##### Invariante de la iteración:
$$\text{Inv}(i, n, ac) \equiv 0 \le i \le n \land ac = |\{k \in \mathbb{N} \mid 0 \le k < i \land a(k) \ge 0 \land cap(aula(a(k))) < est(curso(k))\}|$$

##### Demostración por Inducción sobre la Iteración:

**1. Base Inicial:** Evaluamos el invariante en el estado inicial $s_0 = (0, n, 0)$:
$$\text{Inv}(0, n, 0) \equiv 0 \le 0 \le n \land 0 = |\{k \in \mathbb{N} \mid 0 \le k < 0 \dots \}|$$
El rango $0 \le k < 0$ representa un conjunto vacío. El cardinal de un conjunto vacío es rigurosamente $0$. Por lo tanto, $\text{Inv}(s_0)$ es verdadero.

**2. Paso Inductivo:** Asumimos como hipótesis de inducción que el invariante se cumple para un estado intermedio $s = (i, n, ac)$ con $i < n$. Demostraremos que al aplicar la transformación hacia el estado siguiente $s' = (i + 1, n, ac')$, el invariante $\text{Inv}(s')$ sigue siendo válido.

Por definición de la transformación del acumulador:
$$ac' = |\{k \in \mathbb{N} \mid 0 \le k < i + 1 \land \text{condición}(k)\}|$$

Separamos el conjunto de índices en la unión disjunta del rango ya procesado ($0 \le k < i$) y el nuevo elemento aislado ($k = i$):
$$ac' = |\{k \in \mathbb{N} \mid 0 \le k < i \land \text{condición}(k)\}| + |\{k = i \mid \text{condición}(i)\}|$$

Sustituyendo por nuestra hipótesis de inducción y por la función matemática de evaluación:
$$ac' = ac + \text{evaluar}(i)$$

Esta igualdad matemática es idéntica a la regla de asignación del acumulador interno que gestiona el método de alto orden `.count` en Scala.

## **Conclusión de la demostración:** 
Al cumplirse la base y el paso inductivo, el invariante es válido para cualquier transición. Cuando el combinador alcanza el estado final $s_f$ (donde $i = n$), se asegura que el valor de la variable de retorno $ac$ corresponde exactamente con la cardinalidad de todas las asignaciones defectuosas del dominio. El programa es formalmente correcto.

---

## 2.5 Función desperdicio

```scala
/**
 * Suma de (cap(aula_i) - est(curso_i)) para los cursos asignados
 * con capacidad suficiente.
 */
def desperdicio(cursos: Cursos, aulas: Aulas, a: Asignacion): Int =
  cursos.indices.map { i =>
    if (a(i) >= 0) {
      val dif = capAula(aulas(a(i))) - estCurso(cursos(i))
      math.max(dif, 0)
    } else 0
  }.sum
```

La función calcula el desperdicio total de capacidad generado por una asignación de aulas. Para cada curso asignado se determina cuántos puestos quedan libres en el aula correspondiente y únicamente se acumulan diferencias positivas.

Sea la especificación matemática:

$$
DE(cursos,aulas,a)=
\sum_{i=0}^{n-1}
\max\left(
cap(aulas(a(i)))-
est(cursos(i)),
0
\right)
\quad \text{si } a(i)\ge0
$$

donde:

* $cap(aulas(a(i)))$ representa la capacidad del aula asignada.
* $est(cursos(i))$ representa la cantidad de estudiantes del curso.
* $\max(x,0)$ garantiza que únicamente se contabilicen desperdicios positivos.

##### Modelado de Estados para desperdicio

* **Estado $s$:** Tupla $(i,n,ac)$ donde:

  * $i$ es el índice actual procesado.
  * $n$ es el número total de cursos.
  * $ac$ es el desperdicio acumulado hasta el momento.

* **Estado inicial $s_0$:**

$$
(0,n,0)
$$

* **Estado final $s_f$:**

$$
i=n
$$

* **Transformación de estados:**

$$
(i,n,ac)
\rightarrow
(i+1,n,ac+\text{desp}(i))
$$

donde:

$$
\text{desp}(i)=
\begin{cases}
\max(cap(aulas(a(i)))-est(cursos(i)),0)
&
\text{si } a(i)\ge0
\
0
&
\text{en otro caso}
\end{cases}
$$

##### Invariante de la iteración

$$
Inv(i,n,ac)
\equiv
0\le i\le n
\land
ac=
\sum_{k=0}^{i-1}
\text{desp}(k)
$$

Es decir, en cualquier instante de la ejecución, el acumulador contiene exactamente la suma de los desperdicios de todos los cursos procesados hasta el índice $i-1$.

##### Demostración por Inducción sobre la Iteración

**1. Caso base**

Evaluamos el invariante en el estado inicial:

$$
s_0=(0,n,0)
$$

Sustituyendo en el invariante:

$$
Inv(0,n,0)
\equiv
0\le0\le n
\land
0=
\sum_{k=0}^{-1}
\text{desp}(k)
$$

La suma sobre un conjunto vacío es igual a $0$, por lo tanto el invariante se cumple en el estado inicial.

---

**2. Paso inductivo**

Supongamos que el invariante se cumple para un estado arbitrario:

$$
(i,n,ac)
$$

con:

$$
ac=
\sum_{k=0}^{i-1}
\text{desp}(k)
$$

Aplicando la transformación de estados obtenemos:

$$ac' = ac + desp(i)$$

Sustituyendo la hipótesis inductiva:

$$ac' = \sum_{k=0}^{i-1} desp(k) + desp(i)$$

que equivale a:

$$ac' = \sum_{k=0}^{i} desp(k)$$

Por lo tanto el invariante también se mantiene para el estado siguiente:

$$(i+1,n,ac')$$

---

### Conclusión de la demostración

Se verificó que:

1. El invariante es verdadero en el estado inicial.
2. El invariante se preserva en cada transición de estado.

Por el principio de inducción sobre la iteración, cuando la función alcanza el estado final $i=n$, el acumulador contiene exactamente:

$$
\sum_{i=0}^{n-1}
\text{desp}(i)
$$

que coincide con la especificación matemática del desperdicio total.

Por lo tanto:

$$P_{DE}(cursos,aulas,a) = DE(cursos,aulas,a)$$

y la función `desperdicio` es formalmente correcta.

---

## 2.6 Función movilidad

```scala
/**
 * Ordena los cursos asignados por hora de inicio y suma las distancias
 * entre aulas de cursos consecutivos.
 */
def movilidad(cursos: Cursos, aulas: Aulas, d: Distancias,
              a: Asignacion): Int =
  cursos.indices
    .filter(i => a(i) >= 0)
    .sortBy(i => iniCurso(cursos(i)))
    .sliding(2)
    .map { par =>
      d(a(par(0)))(a(par(1)))
    }.sum
```

La función calcula el costo total de movilidad entre aulas. Para ello considera únicamente los cursos asignados, los ordena cronológicamente y suma las distancias entre las aulas de cada par consecutivo de cursos.

Sea $c_0,c_1,\dots,c_{m-1}$ la secuencia de cursos asignados obtenida después de aplicar el filtrado de cursos válidos y el ordenamiento cronológico realizado por la función.

Sea la especificación matemática:

$$MV(cursos,a,d)=\sum_{k=0}^{m-2} d(a(c_k),a(c_{k+1}))$$

donde:

* $c_0,c_1,\dots,c_{m-1}$ representan los cursos asignados ordenados por hora de inicio.
* $d(x,y)$ corresponde a la distancia entre las aulas $x$ e $y$.

##### Modelado de Estados para movilidad

* **Estado $s$:** Tupla $(i,m,ac)$ donde:

  * $i$ representa el número de pares consecutivos ya procesados.
  * $m$ corresponde al número de cursos asignados.
  * $ac$ almacena la movilidad acumulada.

* **Estado inicial $s_0$:**

$$(0,m,0)$$

* **Estado final $s_f$:**

$$i=m-1$$

* **Transformación de estados:**

$$(i,m,ac)\rightarrow(i+1,m,ac+dist(i))$$

donde:

$$dist(i)=d(a(c_i),a(c_{i+1}))$$

##### Invariante de la iteración

$$Inv(i,m,ac)\equiv 0\le i\le m-1 \land ac=\sum_{k=0}^{i-1} dist(k)$$

Es decir, en cualquier instante del procesamiento, el acumulador contiene exactamente la suma de las distancias correspondientes a todos los pares consecutivos ya evaluados.

##### Demostración por Inducción sobre la Iteración

**1. Caso base**

Evaluamos el invariante en el estado inicial:

$$s_0=(0,m,0)$$

Sustituyendo en el invariante:

$$Inv(0,m,0)\equiv 0\le0\le m-1 \land 0=\sum_{k=0}^{-1} dist(k)$$

La suma sobre un conjunto vacío es igual a $0$, por lo que el invariante se cumple en el estado inicial.

---

**2. Paso inductivo**

Supongamos que el invariante se cumple para un estado arbitrario:

$$(i,m,ac)$$

con:

$$ac=\sum_{k=0}^{i-1} dist(k)$$

Aplicando la transformación de estados obtenemos:

$$ac'=ac+dist(i)$$

Sustituyendo la hipótesis inductiva:

$$ac'=\sum_{k=0}^{i-1} dist(k)+dist(i)$$

que equivale a:

$$ac'=\sum_{k=0}^{i} dist(k)$$

Por lo tanto el invariante también se mantiene para el estado siguiente:

$$(i+1,m,ac')$$

---

### Conclusión de la demostración

Se verificó que:

1. El invariante es verdadero en el estado inicial.
2. El invariante se preserva en cada transición de estado.

Por el principio de inducción sobre la iteración, cuando la función alcanza el estado final, el acumulador contiene exactamente:

$$\sum_{k=0}^{m-2} dist(k)$$

que coincide con la especificación matemática de la movilidad total.

Por lo tanto:

$$P_{MV}(cursos,a,d)=MV(cursos,a,d)$$

y la función `movilidad` es formalmente correcta.

---

## 2.7 Función costoAsignacion

```scala
/** Costo total: w_CH * CH + w_CF * CF + w_DE * DE + w_MV * MV. */
def costoAsignacion(cursos: Cursos, aulas: Aulas, d: Distancias,
                    a: Asignacion, w: Pesos): Int = {

  // Guardamos cada peso en una variable para usar la fórmula más fácilmente
  val (wCH, wCF, wDE, wMV) = w

  wCH * choques(cursos, a) +
  wCF * capacidadFallida(cursos, aulas, a) +
  wDE * desperdicio(cursos, aulas, a) +
  wMV * movilidad(cursos, aulas, d, a)
}
```

La función calcula el costo total de una asignación de aulas mediante una combinación lineal ponderada de las métricas definidas previamente.

Sea la especificación matemática:

$$CT=w_{CH}\cdot CH+w_{CF}\cdot CF+w_{DE}\cdot DE+w_{MV}\cdot MV$$

donde:

* $CH$ representa el número de choques de horario.
* $CF$ representa la cantidad de fallos de capacidad.
* $DE$ representa el desperdicio total de capacidad.
* $MV$ representa el costo total de movilidad.

##### Modelo de evaluación para costoAsignacion

La función no realiza iteraciones explícitas ni transformaciones sucesivas de una colección.

Su evaluación puede modelarse mediante el estado:

$$s=(CH,CF,DE,MV,CT)$$

donde:

* $CH$ corresponde al resultado de `choques`.
* $CF$ corresponde al resultado de `capacidadFallida`.
* $DE$ corresponde al resultado de `desperdicio`.
* $MV$ corresponde al resultado de `movilidad`.
* $CT$ corresponde al costo total calculado.

##### Estado inicial

El estado inicial se obtiene después de evaluar las cuatro métricas:

$$s_0=(CH,CF,DE,MV,0)$$

##### Estado final

El estado final corresponde al cálculo completo del costo:

$$s_f=(CH,CF,DE,MV,CT)$$

donde:

$$CT=w_{CH}CH+w_{CF}CF+w_{DE}DE+w_{MV}MV$$

##### Invariante

$$Inv(CH,CF,DE,MV,CT)\equiv CT=w_{CH}CH+w_{CF}CF+w_{DE}DE+w_{MV}MV$$

Es decir, el valor almacenado en $CT$ debe coincidir exactamente con la combinación ponderada de las métricas calculadas previamente.

##### Demostración de Corrección

Las funciones:

$$choques(cursos,a)$$

$$capacidadFallida(cursos,aulas,a)$$

$$desperdicio(cursos,aulas,a)$$

$$movilidad(cursos,aulas,d,a)$$

ya fueron demostradas correctas respecto a sus respectivas especificaciones.

Por lo tanto:

$$P_{CH}=CH$$

$$P_{CF}=CF$$

$$P_{DE}=DE$$

$$P_{MV}=MV$$

Sustituyendo estos resultados en la implementación de `costoAsignacion`:

$$CT=w_{CH}\cdot P_{CH}+w_{CF}\cdot P_{CF}+w_{DE}\cdot P_{DE}+w_{MV}\cdot P_{MV}$$

Aplicando la corrección de cada una de las funciones auxiliares:

$$CT=w_{CH}\cdot CH+w_{CF}\cdot CF+w_{DE}\cdot DE+w_{MV}\cdot MV$$

que coincide exactamente con la especificación matemática definida para el costo total.

---

### Conclusión de la demostración

Dado que cada métrica utilizada por la función es correcta respecto a su especificación y la implementación aplica exactamente la fórmula matemática definida en el problema, se concluye que:

$$P_{CT}(cursos,aulas,d,a,w)=CT(cursos,aulas,d,a,w)$$

Por lo tanto, la función `costoAsignacion` es formalmente correcta.

---

## 2.8 Función `generarAsignaciones`

```scala
def generarAsignaciones(n: Int, m: Int): Vector[Asignacion] = {
  if (n == 0) {
    Vector(Vector.empty[Int])
  } else {
    generarAsignaciones(n - 1, m).flatMap { asignacionParcial =>
      Vector.tabulate(m)(k => asignacionParcial :+ k)
    }
  }
}
```

### Especificación matemática

Sea $n \in \mathbb{N}$ el número de cursos y $m \in \mathbb{N}$ el número de aulas, con $m \ge 1$. Definimos el conjunto de todas las asignaciones completas posibles como:

$$
\mathcal{A}(n,m) = \{\, \alpha \in \{0,1,\ldots,m-1\}^n \,\}.
$$

La especificación matemática de la función es:

$$
f_{\mathrm{gen}}(n,m) = \mathcal{A}(n,m).
$$

Es decir, para cada par $(n,m)$, la función debe devolver exactamente todos los vectores de longitud $n$ con entradas en $\{0,\ldots,m-1\}$, sin repetir ni omitir ninguno.

### Idea recursiva

Observamos que el conjunto $\mathcal{A}(n,m)$ se puede definir recursivamente sobre $n$:

- Para $n = 0$, solo existe el vector vacío:

$$
\mathcal{A}(0,m) = \{ \langle\,\rangle \}.
$$

- Para $n > 0$, cualquier $\alpha \in \mathcal{A}(n,m)$ se puede escribir como $\beta \frown \langle k \rangle$, donde $\beta \in \mathcal{A}(n-1,m)$ y $k \in \{0,\ldots,m-1\}$. Entonces:

$$
\mathcal{A}(n,m) =
\{\, \beta \frown \langle k \rangle
\mid \beta \in \mathcal{A}(n-1,m),\ 0 \le k < m \,\}.
$$

La implementación en Scala replica exactamente esta definición: el caso base $n=0$ devuelve un único vector vacío, y el caso recursivo toma todas las asignaciones de tamaño $n-1$ y, para cada una, agrega todas las posibles aulas $k \in \{0,\ldots,m-1\}$ al final usando `:+ k`.

### Correctitud por inducción estructural sobre $n$

Demostraremos que:

$$
\forall n \in \mathbb{N}\ \forall m \in \mathbb{N} :
\texttt{generarAsignaciones}(n,m) = f_{\mathrm{gen}}(n,m).
$$

### **Caso base:** $n = 0$.

La implementación devuelve `Vector(Vector.empty[Int])`, es decir el conjunto $\{ \langle\,\rangle \}$.

Matemáticamente, por definición:

$$
f_{\mathrm{gen}}(0,m) = \mathcal{A}(0,m) = \{ \langle\,\rangle \}.
$$

Por lo tanto:

$$
\texttt{generarAsignaciones}(0,m) = f_{\mathrm{gen}}(0,m).
$$

### **Caso inductivo:** 
Supongamos que para algún $n \ge 1$ se cumple la hipótesis de inducción:

$$
\texttt{generarAsignaciones}(n-1,m) = \mathcal{A}(n-1,m).
$$

La implementación para $n$ calcula:

- `prev = generarAsignaciones(n - 1, m)`, y por HI, `prev` contiene exactamente todos los vectores de longitud $n-1$ sobre $\{0,\ldots,m-1\}$.
- Luego aplica:

$$
\texttt{prev.flatMap}\big(\lambda \beta.\ \texttt{Vector.tabulate}(m)(k \mapsto \beta :+ k)\big).
$$

Esto produce el conjunto:

$$
\{\, \beta \frown \langle k \rangle
\mid \beta \in \mathcal{A}(n-1,m),\ 0 \le k < m \,\},
$$

ya que para cada $\beta$ genera exactamente los $m$ vectores $\beta :+ 0, \beta :+ 1, \ldots, \beta :+ (m-1)$.

Por definición de $\mathcal{A}(n,m)$, ese conjunto es precisamente $\mathcal{A}(n,m)$. Por lo tanto:

$$
\texttt{generarAsignaciones}(n,m) = \mathcal{A}(n,m) = f_{\mathrm{gen}}(n,m),
$$

cerrando el paso inductivo.

### **Conclusión**

Por inducción estructural sobre $n$, la función `generarAsignaciones` devuelve exactamente el conjunto $\mathcal{A}(n,m)$ de todas las asignaciones completas posibles en $\{0,\ldots,m-1\}^n$, cumpliendo la especificación del punto 2.8 del proyecto.

---

## 2.9 Función `asignacionOptima`

```scala
def asignacionOptima(cursos: Cursos, aulas: Aulas, d: Distancias,
                     w: Pesos): (Asignacion, Int) = {
  val n = cursos.length
  val m = aulas.length

  val asignaciones: Vector[Asignacion] = generarAsignaciones(n, m)

  val asignacionesConCosto: Vector[(Asignacion, Int)] =
    asignaciones.map { a =>
      val c = costoAsignacion(cursos, aulas, d, a, w)
      (a, c)
    }

  asignacionesConCosto.minBy(_._2)
}
```

### Especificación matemática

El problema formal define, para un conjunto fijo de cursos $C$, un conjunto de aulas $A$, una matriz de distancias $DA$ y pesos $w$, la función de costo total $CT_\alpha(C,A,DA,w)$ asociada a cada asignación completa $\alpha$. La especificación de la asignación óptima es:

```math
f_{\mathrm{opt}}(C,A,DA,w) =
\min_{\alpha \in \mathcal{A}(n,m)} CT_{\alpha}(C,A,DA,w)
```

donde $\mathcal{A}(n,m)$ es el conjunto de todas las asignaciones completas:

$$
\mathcal{A}(n,m) = \{\, \alpha \in \{0,\ldots,m-1\}^n \,\}.
$$

En términos del resultado de la función, queremos que `asignacionOptima` devuelva un par $(\alpha^\*, CT_{\alpha^\*})$ tal que:

$$
\alpha^\* \in \mathcal{A}(n,m)
\quad\text{y}\quad
\forall \beta \in \mathcal{A}(n,m):\ CT_{\alpha^\*} \le CT_\beta.
$$

Recordemos que `costoAsignacion` implementa exactamente la función $CT_\alpha$ definida en la sección 1.1.4 del enunciado, y ya fue demostrada correcta en el punto 2.7 del informe.

### Descomposición funcional de la implementación

La función `asignacionOptima` se puede ver como la composición de tres pasos:

1. **Enumeración del dominio de búsqueda:**

$$
\texttt{asignaciones} = \texttt{generarAsignaciones}(n,m).
$$

Por el resultado de 2.8, esto equivale a:

$$
\texttt{asignaciones} = \mathcal{A}(n,m).
$$

2. **Cálculo del costo para cada asignación:**

La comprensión:

```scala
val asignacionesConCosto =
  asignaciones.map { a =>
    val c = costoAsignacion(cursos, aulas, d, a, w)
    (a, c)
  }
```

construye el vector:

$$
\texttt{asignacionesConCosto} =
\big[\, (\alpha, CT_\alpha) \mid \alpha \in \mathcal{A}(n,m) \,\big],
$$

ya que `costoAsignacion` calcula exactamente $CT_\alpha$ para cada $\alpha$.

3. **Selección de la tupla con menor costo:**

```scala
asignacionesConCosto.minBy(_._2)
```

devuelve un par $(\alpha^{*}, c^{*})$ donde $c^{*}$ es el mínimo valor entre todos los segundos componentes:

```math
(\alpha^{*}, c^{*}) =
\min_{(\alpha,c) \in A} c
```

donde $A$ denota el conjunto de todas las parejas $(\alpha,c)$ en `asignacionesConCosto`.

Dado que $c = CT_{\alpha}$, esto equivale a:

```math
(\alpha^{*}, c^{*}) =
\left(
\min_{\alpha \in \mathcal{A}(n,m)} CT_{\alpha},
\min_{\alpha \in \mathcal{A}(n,m)} CT_{\alpha}
\right)
```

### Correctitud de `asignacionOptima`

Probamos que la salida de la función cumple con la especificación:

1. **La asignación devuelta es completa.**

Como `generarAsignaciones(n,m)` solo produce vectores en $\{0,\ldots,m-1\}^n$, cualquier `a` en `asignaciones` cumple:

$$
\forall i \in \{0,\ldots,n-1\} : 0 \le a(i) \le m-1.
$$

Es decir, todos los cursos están asignados a alguna aula (no aparecen valores $-1$). El par devuelto por `minBy(_._2)` toma su primer componente de esta colección, por lo que $\alpha^\* \in \mathcal{A}(n,m)$.

2. **El costo de la asignación devuelta es mínimo.**

Sea:

$$
L = \big[ (\alpha_0, CT_{\alpha_0}),
(\alpha_1, CT_{\alpha_1}),
\ldots,
(\alpha_{k-1}, CT_{\alpha_{k-1}}) \big]
$$

el vector `asignacionesConCosto`. Por definición de `minBy(_._2)`, el par seleccionado $(\alpha^\*, CT_{\alpha^\*})$ satisface:

- Existe $j$ tal que $(\alpha^\*, CT_{\alpha^\*}) = (\alpha_j, CT_{\alpha_j})$.
- Para todo $i$ con $0 \le i < k$:

$$
CT_{\alpha^\*} \le CT_{\alpha_i}.
$$

Dado que cada $\alpha_i$ recorre exactamente $\mathcal{A}(n,m)$, la condición anterior se reescribe como:

$$
\forall \beta \in \mathcal{A}(n,m):\ CT_{\alpha^\*} \le CT_\beta,
$$

que es precisamente la definición de asignación óptima requerida en el problema.

### Conclusión

- `generarAsignaciones` explora exhaustivamente el espacio $\mathcal{A}(n,m)$ de asignaciones completas.
- `costoAsignacion` calcula correctamente $CT_\alpha$ para cada asignación $\alpha$.
- `asignacionOptima` aplica `costoAsignacion` a todas las asignaciones completas y selecciona con `minBy(_._2)` aquella con costo mínimo.

Por lo tanto, se cumple:

$$
P_{\mathrm{opt}}(C,A,DA,w) =
f_{\mathrm{opt}}(C,A,DA,w),
$$

y la función `asignacionOptima` es formalmente correcta de acuerdo con la especificación del punto 2.9.
