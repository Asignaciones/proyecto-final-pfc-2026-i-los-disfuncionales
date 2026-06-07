package proyecto

import common._
import AsignacionAulas._

object AsignacionAulasPar {

  /**
   * Versión paralela de choques
   * divide los indices de los cursos en dos mitades y calcula
   * simultaneamente el numero de choques en cada parte usando
   * la funcion parallel finalmente suma ambos resultados
   * un choque ocurre cuando dos cursos que se traslapan en el
   * tiempo están asignados a la misma aula
   */
  def choquesPar(cursos: Cursos, a: Asignacion): Int = {
    def contar(inicio: Int, fin: Int): Int =
      (inicio until fin).map { i=>
        cursos.indices.drop(i + 1).count { j =>
          a(i) >= 0 &&
            a(i) == a(j) &&
            solapan(cursos(i), cursos(j))
        }
      }.sum

    val mitad = cursos.length / 2

    val (izq, der) = parallel(
      contar(0, mitad),
      contar(mitad, cursos.length)
    )

    izq + der
  }

  /**
   * Versión paralela de desperdicio
   * divide los cursos en dos mitades y calcula de forma
   * concurrente el desperdicio generado por cada grupo
   * el desperdicio corresponde a los puestos vacios de las
   * aulas que tienen capacidad suficiente para el curso
   */
  def desperdicioPar(cursos: Cursos, aulas: Aulas, a: Asignacion): Int = {

    def suma(inicio: Int, fin: Int): Int =
      (inicio until fin).map { i =>
        if (a(i) >= 0) {
          val dif = capAula(aulas(a(i))) - estCurso(cursos(i))
          math.max(dif, 0)
        } else 0
      }.sum

    val mitad = cursos.length / 2

    val (izq, der) = parallel(
      suma(0, mitad),
      suma(mitad, cursos.length)
    )

    izq + der
  }

  /**
   * Versión paralela de movilidad
   * para cumplir la estrategia propuesta en el enunciado, el vector
   * de cursos se divide en dos mitades cada mitad calcula de forma
   * concurrente la distancia recorrida entre aulas de los cursos
   * asignados que contiene los resultados parciales se ejecutan mediante parallel y luego
   * se combinan sumando ambas contribuciones
   */
  def movilidadPar(
                    cursos: Cursos,
                    aulas: Aulas,
                    d: Distancias,
                    a: Asignacion
                  ): Int = {

    val mitad = cursos.length / 2

    def movilidadRango(inicio: Int, fin: Int): Int =
      (inicio until fin)
        .filter(i => a(i) >= 0)
        .sortBy(i => iniCurso(cursos(i)))
        .sliding(2)
        .collect {
          case Seq(i, j) =>
            d(a(i))(a(j))
        }
        .sum

    val (izq, der) = parallel(
      movilidadRango(0, mitad),
      movilidadRango(mitad, cursos.length)
    )

    izq + der
  }

  /**
   * Versión paralela de generarAsignaciones
   * genera todas las asignaciones posibles paralelizando unicamente
   * los valores que puede tomar el primer curso, tal como indica el
   * enunciado del proyecto primero se generan secuencialmente las asignaciones de los n-1
   * cursos restantes luego los posibles valores del primer curso
   * se dividen en dos grupos y cada grupo construye sus asignaciones
   * de forma concurrente mediante parallel
   */
  def generarAsignacionesPar(n: Int, m: Int): Vector[Asignacion] = {

    if (n == 0)
      Vector(Vector.empty[Int])

    else {

      val prev = generarAsignaciones(n - 1, m)

      val mitad = m / 2

      val (izq, der) = parallel(

        (0 until mitad).toVector.flatMap { k =>
          prev.map(k +: _)
        },

        (mitad until m).toVector.flatMap { k =>
          prev.map(k +: _)
        }
      )

      izq ++ der
    }
  }

  /**
   * Versión paralela de asignacionOptima
   * genera todas las asignaciones candidatas y divide el espacio
   * de busqueda en dos mitades cada mitad busca de forma
   * concurrente la asignacion con menor costo
   * finalmente compara ambos minimos y devuelve el mejor
   */
  def asignacionOptimaPar(
                           cursos: Cursos,
                           aulas: Aulas,
                           d: Distancias,
                           w: Pesos
                         ): (Asignacion, Int) = {

    val asignaciones =
      generarAsignacionesPar(cursos.length, aulas.length)

    val mitad = asignaciones.length / 2

    def mejor(
               candidatos: Vector[Asignacion]
             ): (Asignacion, Int) = {

      candidatos
        .map(a => (a, costoAsignacion(cursos, aulas, d, a, w)))
        .minBy(_._2)
    }

    val (izq, der) = parallel(
      mejor(asignaciones.take(mitad)),
      mejor(asignaciones.drop(mitad))
    )

    if (izq._2 <= der._2) izq else der
  }
}
