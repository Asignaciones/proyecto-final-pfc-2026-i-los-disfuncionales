package proyecto

object App {
  def main(args: Array[String]): Unit = {
    println("Proyecto Final - Asignación Óptima de Aulas")
    println("Implemente AsignacionAulas y AsignacionAulasPar para comenzar.")

    /* lo usado para medir tiempos
    * val pesos = (1000, 100, 1, 2)

    val casos = List(
      (4, 3),
      (6, 4),
      (7, 5),
      (8, 5)
    )

    for ((n, m) <- casos) {
      val cursos = cursosAlAzar(n)
      val aulas  = aulasAlAzar(m)
      val d      = distanciasAlAzar(m)

      val tiempoSec = measure { asignacionOptima(cursos, aulas, d, pesos) }
      val tiempoPar = measure { asignacionOptimaPar(cursos, aulas, d, pesos) }

      val aceleracion = (1.0 - tiempoPar.value / tiempoSec.value) * 100
      println(f"n=$n, m=$m | Sec: $tiempoSec | Par: $tiempoPar | Aceleración: $aceleracion%.1f%%")
    }
     */
  }
}
