# Ejemplo informe de corrección

**Fundamentos de Programación Funcional y Concurrente**  
Documento realizado por el docente Juan Francisco Díaz.

---

## Argumentación de corrección de programas

### Argumentando sobre corrección de programas recursivos

Sea $f : A \to B$ una función, y $A$ un conjunto definido recursivamente (recordar definición de matemáticas discretas I), como por ejemplo los naturales o las listas.

Sea $P_f$ un programa recursivo (lineal o en árbol) desarrollado en Scala (o en cualquier lenguaje de programación) hecho para calcular $f$:

```scala
def Pf(a: A): B = { // Pf recibe a de tipo A, y devuelve f(a) de tipo B
  ...
}
```

¿Cómo argumentar que \$P_f(a)\$ siempre devuelve \$f(a)\$ como respuesta? Es decir, ¿cómo argumentar que \$P_f\$ es correcto con respecto a su especificación?

La respuesta es sencilla, demostrando el siguiente teorema:

$$
\forall a \in A : P_f(a) == f(a)
$$

Cuando uno tiene que demostrar que algo se cumple para todos los elementos de un conjunto definido recursivamente, es natural usar **inducción estructural**.

En términos prácticos, esto significa demostrar que:

- Para cada valor básico \$a\$ de \$A\$, se tiene que \$P_f(a) == f(a)\$.
- Para cada valor \$a \in A\$ construido recursivamente a partir de otro(s) valor(es) \$a' \in A\$, se tiene que \$P_f(a') == f(a') \rightarrow P_f(a) == f(a)\$ (hipótesis de inducción).

---

#### Ejemplo: Factorial Recursivo

Sea \$f : \mathbb{N} \to \mathbb{N}\$ la función que calcula el factorial de un número natural, \$f(n) = n!\$.

Programa en Scala:

```scala
def Pf(n: Int): Int = {
  if (n == 0) 1 else n * Pf(n - 1)
}
```

Queremos demostrar que:

$$
\forall n \in \mathbb{N} : P_f(n) == n!
$$

- **Caso base**: \$n = 0\$

$$
P_f(0) \to 1 \quad \land \quad f(0) = 0! = 1
$$

Entonces \$P_f(0) == f(0)\$.

- **Caso inductivo**: \$n = k+1\$, \$k \geq 0\$.

$$
P_f(k+1) \to (k+1) \cdot P_f(k)
$$

Usando la hipótesis de inducción:

$$
\to (k+1) \cdot k! = (k+1)!
$$

Por lo tanto, \$P_f(k+1) == f(k+1)\$.

**Conclusión**: \$\forall n \in \mathbb{N} : P_f(n) == n!\$

---

#### Ejemplo: El máximo de una lista

Sea \$f : \text{List}\[\mathbb{N}] \to \mathbb{N}\$ la función que calcula el máximo de una lista no vacía.

Programa en Scala:

```scala
def maxLin(l: List[Int]): Int = {
  if (l.tail.isEmpty) l.head
  else math.max(maxLin(l.tail), l.head)
}
```

Queremos demostrar que:

$$
\forall n \in \mathbb{N} \setminus \{0\} :
P_f(\text{List}(a_1, \ldots, a_n)) == f(\text{List}(a_1, \ldots, a_n))
$$

- **Caso base**: \$n=1\$.

$$
P_f(\text{List}(a_1)) \to a_1 \quad \land \quad f(\text{List}(a_1)) = a_1
$$

- **Caso inductivo**: \$n=k+1\$.

$$
P_f(L) \to \text{math.max}(P_f(\text{List}(a_2, \ldots, a_{k+1})), a_1)
$$

Dependiendo del mayor entre \$a_1\$ y \$b\$ (el máximo del resto de la lista), se cumple que \$P_f(L) == f(L)\$.

**Conclusión**:

$$
\forall n \in \mathbb{N} \setminus \{0\} : P_f(\text{List}(a_1, \ldots, a_n)) == f(\text{List}(a_1, \ldots, a_n))
$$

---

### Argumentando sobre corrección de programas iterativos

Para argumentar la corrección de programas iterativos, se debe formalizar cómo es la iteración:

- Representación de un estado \$s\$.
- Estado inicial \$s_0\$.
- Estado final \$s_f\$.
- Invariante de la iteración \$\text{Inv}(s)\$.
- Transformación de estados \$\text{transformar}(s)\$.

Programa iterativo genérico:

```scala
def Pf(a: A): B = {
  def Pf_iter(s: Estado): B =
    if (esFinal(s)) respuesta(s) else Pf_iter(transformar(s))
  Pf_iter(s0)
}
```

---

#### Ejemplo: Factorial Iterativo

```scala
def Pf(n: Int): Int = {
  def Pf_iter(i: Int, n: Int, ac: Int): Int =
    if (i > n) ac else Pf_iter(i + 1, n, i * ac)
  Pf_iter(1, n, 1)
}
```

- Estado \$s = (i, n, ac)\$
- Estado inicial \$s_0 = (1, n, 1)\$
- Estado final: \$i = n+1\$
- Invariante: \$\text{Inv}(i,n,ac) \equiv i \leq n+1 \land ac = (i-1)!\$
- Transformación: \$(i, n, ac) \to (i+1, n, i \cdot ac)\$

Por inducción sobre la iteración, se demuestra que al llegar a \$s_f\$, \$ac = n!\$.

---

#### Ejemplo: El máximo de una lista

```scala
def maxIt(l: List[Int]): Int = {
  def maxAux(max: Int, l: List[Int]): Int = {
    if (l.isEmpty) max
    else maxAux(math.max(max, l.head), l.tail)
  }
  maxAux(l.head, l.tail)
}
```

- Estado \$s = (max, l)\$
- Estado inicial \$s_0 = (a_1, \text{List}(a_2, \ldots, a_k))\$
- Estado final: \$l = \text{List}()\$
- Invariante: \$\text{Inv}(max, l) \equiv max = f(\text{prefijo})\$
- Transformación: \$(max, l) \to (\text{math.max}(max, l.head), l.tail)\$

Por inducción, al llegar al estado final, \$max = f(L)\$.

**Conclusión**:

$$
P_f(L) == f(L)
$$
---

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

#### 2.5 Función desperdicio

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

#### 2.6 Función movilidad

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

#### 2.7 Función costoAsignacion

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
