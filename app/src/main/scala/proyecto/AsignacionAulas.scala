package proyecto

import scala.util.Random

object AsignacionAulas {

  // Un curso es (id, horaInicio, horaFin, numEstudiantes).
  // Las horas son bloques de 30 min desde las 6:00 a.m. (p. ej. ini=4 → 8:00 a.m.).
  type Curso = (String, Int, Int, Int)
  type Cursos = Vector[Curso]

  // Un aula es (id, capacidad).
  type Aula = (String, Int)
  type Aulas = Vector[Aula]

  // Asignacion(i) = j significa que el curso i se dicta en el aula j; -1 = sin asignar.
  type Asignacion = Vector[Int]

  // Matriz simétrica de distancias entre aulas.
  type Distancias = Vector[Vector[Int]]

  // Pesos: (w_CH, w_CF, w_DE, w_MV).
  type Pesos = (Int, Int, Int, Int)

  // ---------------------------------------------------------------------------
  // Funciones de generación (ya implementadas — NO MODIFICAR)
  // ---------------------------------------------------------------------------

  val random = new Random()

  def cursosAlAzar(n: Int): Cursos =
    Vector.tabulate(n) { i =>
      val ini = random.nextInt(29)
      val dur = random.nextInt(7) + 2
      ("C" + i, ini, ini + dur, random.nextInt(46) + 5)
    }

  def aulasAlAzar(m: Int): Aulas =
    Vector.tabulate(m)(j => ("E" + j, random.nextInt(46) + 15))

  def distanciasAlAzar(m: Int): Distancias = {
    val v = Vector.fill(m, m)(random.nextInt(m * 2) + 1)
    Vector.tabulate(m, m)((i, j) =>
      if (i < j) v(i)(j)
      else if (i == j) 0
      else v(j)(i))
  }

  // ---------------------------------------------------------------------------
  // Funciones de acceso (ya implementadas — NO MODIFICAR)
  // ---------------------------------------------------------------------------

  def idCurso(c: Curso): String = c._1
  def iniCurso(c: Curso): Int   = c._2
  def finCurso(c: Curso): Int   = c._3
  def estCurso(c: Curso): Int   = c._4

  def idAula(a: Aula): String   = a._1
  def capAula(a: Aula): Int     = a._2

  // ---------------------------------------------------------------------------
  // Funciones a implementar
  // ---------------------------------------------------------------------------
    //Samuel
  /** Devuelve true sii los intervalos [ini1, fin1) y [ini2, fin2) se traslapan. */
  def solapan(c1: Curso, c2: Curso): Boolean =
    iniCurso(c1) < finCurso(c2) && iniCurso(c2) < finCurso(c1)

  /**
   * Número de pares (i, j) con i < j tales que a(i) == a(j) >= 0
   * y los cursos i y j se solapan.
   */
    //Samuel
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
    //Samuel
  /** Cantidad de cursos cuya aula asignada tiene capacidad menor al número de estudiantes. */
  def capacidadFallida(cursos: Cursos, aulas: Aulas, a: Asignacion): Int =
    cursos.indices          // genera los índices 0..n-1
      .count { i =>         // cuenta directamente los que cumplan la condición
        a(i) >= 0 &&        // el curso está asignado a algún aula
          capAula(aulas(a(i))) < estCurso(cursos(i))  // el aula no alcanza para los estudiantes
      }
  /**
   * Suma de (cap(aula_i) - est(curso_i)) para los cursos asignados
   * con capacidad suficiente.
   */
    //Santiago
  def desperdicio(cursos: Cursos, aulas: Aulas, a: Asignacion): Int =
    cursos.indices.map { i =>
      // Si el curso tiene un aula asignada, calculamos cuánto espacio sobra
      if (a(i) >= 0) {
        val dif = capAula(aulas(a(i))) - estCurso(cursos(i))
        math.max(dif, 0) // Si la diferencia es negativa, aporta 0 al desperdicio
      } else 0 // Los cursos sin asignar no generan desperdicio
    }.sum // Sumamos el desperdicio de todos los cursos

  /**
   * Ordena los cursos asignados por hora de inicio y suma las distancias
   * entre aulas de cursos consecutivos.
   */
    //Santiago
    // sortBy: Ordena los elementos de una colección según el criterio que se le indique.
    // sliding(2): Crea ventanas consecutivas de tamaño 2 sobre una colección.
  def movilidad(cursos: Cursos, aulas: Aulas, d: Distancias,
                a: Asignacion): Int =
    cursos.indices
      .filter(i => a(i) >= 0) // Tomamos únicamente los cursos que tienen aula asignada
      .sortBy(i => iniCurso(cursos(i))) // Los ordenamos por hora de inicio
      .sliding(2) // Formamos pares de cursos consecutivos
      .map { par =>
        d(a(par(0)))(a(par(1))) // Distancia entre las aulas de cada par consecutivo
      }.sum // Sumamos todas las distancias recorridas

  //Santiago
  /** Costo total: w_CH * CH + w_CF * CF + w_DE * DE + w_MV * MV. */
  def costoAsignacion(cursos: Cursos, aulas: Aulas, d: Distancias,
                      a: Asignacion, w: Pesos): Int = {
    // Guardamos cada peso en una variable para usar la fórmula más fácilmente
    val (wCH, wCF, wDE, wMV) = w
      wCH * choques(cursos, a) + // Penalización por choques de horario
      wCF * capacidadFallida(cursos, aulas, a) + // Penalización por aulas con capacidad insuficiente
      wDE * desperdicio(cursos, aulas, a) + // Espacios desaprovechados en las aulas
      wMV * movilidad(cursos, aulas, d, a) // Distancia recorrida entre aulas
  }
  /**
   * Genera todas las asignaciones completas posibles: vectores en {0,..,m-1}^n.
   * El tamaño del resultado es m^n.
   */
  def generarAsignaciones(n: Int, m: Int): Vector[Asignacion] = ???

  /**
   * Devuelve la asignación de mínimo costo y su costo.
   * Usa generarAsignaciones para explorar el espacio.
   */
  def asignacionOptima(cursos: Cursos, aulas: Aulas, d: Distancias,
                       w: Pesos): (Asignacion, Int) = ???
}
