package proyecto

import org.scalatest.funsuite.AnyFunSuite
import org.junit.runner.RunWith
import org.scalatestplus.junit.JUnitRunner
import AsignacionAulas._
import AsignacionAulasPar._

@RunWith(classOf[JUnitRunner])
class AsignacionAulasParTest extends AnyFunSuite {

  val c1: Cursos    = Vector(("M01", 4, 8, 25), ("M02", 6, 10, 30), ("M03", 12, 16, 20))
  val a1: Aulas     = Vector(("E101", 30), ("E102", 40))
  val d1: Distancias = Vector(Vector(0, 3), Vector(3, 0))
  val w: Pesos      = (1000, 100, 1, 2)

  test("choquesPar: asignacion [0,0,1] tiene 1 choque") {
    assert(choquesPar(c1, Vector(0, 0, 1)) == 1)
  }

  test("choquesPar: asignacion [0,1,0] no tiene choques") {
    assert(choquesPar(c1, Vector(0, 1, 0)) == 0)
  }

  test("desperdicioPar: asignacion [0,0,1] tiene desperdicio 25") {
    assert(desperdicioPar(c1, a1, Vector(0, 0, 1)) == 25)
  }

  test("movilidadPar: asignacion [0,0,1] tiene movilidad 3") {
    assert(movilidadPar(c1, a1, d1, Vector(0, 0, 1)) == 3)
  }

  test("generarAsignacionesPar: 2 cursos y 2 aulas produce 4 asignaciones") {
    assert(generarAsignacionesPar(2, 2).length == 4)
  }

  test("asignacionOptimaPar: el costo de la optima no supera el de [0,1,0] (37)") {
    val (_, costo) = asignacionOptimaPar(c1, a1, d1, w)
    assert(costo <= 37)
  }

  // Apartado de Test Propios

  // test propios de choquesPar
  test("choquesPar: todos los cursos en la misma aula generan un choque") {
    // M01 y M02 se traslapan en E101.
    // M03 no se traslapa con ninguno.
    assert(choquesPar(c1, Vector(0, 0, 0)) == 1)
  }

  test("choquesPar: cursos en aulas distintas no generan choques") {
    // Aunque M01 y M02 se traslapan, están en aulas diferentes.
    assert(choquesPar(c1, Vector(0, 1, 1)) == 0)
  }

  test("choquesPar: cursos sin asignar no generan choques") {
    assert(choquesPar(c1, Vector(-1, -1, -1)) == 0)
  }

  test("choquesPar: asignacion parcial conserva los choques validos") {
    // Solo M01 y M02 participan y chocan en E101.
    assert(choquesPar(c1, Vector(0, 0, -1)) == 1)
  }

  test("choquesPar: coincide con la version secuencial") {
    val asignacion = Vector(0, 0, 0)

    assert(
      choquesPar(c1, asignacion) ==
        choques(c1, asignacion)
    )
  }

  // test propios de desperdicioPar
  test("desperdicioPar: capacidad exacta genera desperdicio 0") {

    val cursoExacto = Vector(
      ("M99", 4, 8, 30)
    )

    // E101 tiene capacidad 30.
    // 30 - 30 = 0

    assert(desperdicioPar(cursoExacto, a1, Vector(0)) == 0)
  }

  test("desperdicioPar: cursos sin asignar no generan desperdicio") {

    val curso = Vector(
      ("M99", 4, 8, 20)
    )

    // El curso no tiene aula asignada.

    assert(desperdicioPar(curso, a1, Vector(-1)) == 0)
  }

  test("desperdicioPar: aula insuficiente aporta 0 al desperdicio") {

    val cursoGrande = Vector(
      ("M99", 4, 8, 50)
    )

    // 30 - 50 = -20
    // math.max(-20, 0) = 0

    assert(desperdicioPar(cursoGrande, a1, Vector(0)) == 0)
  }

  test("desperdicioPar: acumula desperdicios de varias aulas") {

    val cursos = Vector(
      ("C1", 4, 8, 20),
      ("C2", 8, 12, 10)
    )

    // E101: 30 - 20 = 10
    // E102: 40 - 10 = 30
    // Total = 40

    assert(desperdicioPar(cursos, a1, Vector(0, 1)) == 40)
  }

  test("desperdicioPar: coincide con la version secuencial") {

    val asignacion = Vector(0, 1, 0)

    assert(
      desperdicioPar(c1, a1, asignacion) ==
        desperdicio(c1, a1, asignacion)
    )
  }

  // test propios de movilidadPar
  test("movilidadPar: dos cursos divididos entre mitades generan movilidad 0") {

    val cursos = Vector(
      ("C1", 4, 8, 20),
      ("C2", 8, 12, 20)
    )

    assert(movilidadPar(cursos, a1, d1, Vector(0,1)) == 0)
  }

  test("movilidadPar: dos cursos en la misma mitad generan movilidad positiva") {

    val cursos = Vector(
      ("C1", 4, 8, 20),
      ("C2", 8, 12, 20),
      ("C3", 20, 24, 20),
      ("C4", 24, 28, 20)
    )

    assert(
      movilidadPar(cursos, a1, d1, Vector(0,1,0,1)) == 6
    )
  }

  test("movilidadPar: un unico curso asignado genera movilidad 0") {

    val curso = Vector(
      ("M99", 4, 8, 20)
    )

    assert(movilidadPar(curso, a1, d1, Vector(0)) == 0)
  }

  test("movilidadPar: todos los cursos en la misma aula generan movilidad 0") {
    assert(
      movilidadPar(c1, a1, d1, Vector(0, 0, 0)) == 0
    )
  }

  test("movilidadPar: asignacion [1,1,0] genera movilidad 3") {
    assert(movilidadPar(c1, a1, d1, Vector(1,1,0)) == 3)
  }

  // test propios de generarAsignacionesPar
  test("generarAsignacionesPar: no genera asignaciones repetidas") {

    val asignaciones =
      generarAsignacionesPar(3,2)

    assert(asignaciones.length == asignaciones.distinct.length)
  }

  test("generarAsignacionesPar: 1 curso y 2 aulas produce 2 asignaciones") {

    val esperado = Vector(
      Vector(0),
      Vector(1)
    )

    assert(generarAsignacionesPar(1, 2).toSet == esperado.toSet)
  }

  test("generarAsignacionesPar: 0 cursos produce una asignacion vacia") {

    val esperado = Vector(
      Vector.empty[Int]
    )

    assert(generarAsignacionesPar(0, 2) == esperado)
  }

  test("generarAsignacionesPar: 2 cursos y 1 aula produce una unica asignacion") {

    val esperado = Vector(
      Vector(0, 0)
    )

    assert(generarAsignacionesPar(2, 1) == esperado)
  }

  test("generarAsignacionesPar: genera las mismas asignaciones que la version secuencial") {

    assert(
      generarAsignacionesPar(3, 2).toSet ==
        generarAsignaciones(3, 2).toSet
    )
  }

  test("generarAsignacionesPar: 3 cursos y 2 aulas produce 8 asignaciones") {

    assert(
      generarAsignacionesPar(3, 2).length == 8
    )
  }

  // test propios de asignacionOptimaPar
  test("asignacionOptimaPar: la asignacion obtenida tiene costo optimo") {

    val (asigPar, costoPar) =
      asignacionOptimaPar(c1, a1, d1, w)

    assert(
      costoAsignacion(c1, a1, d1, asigPar, w) == costoPar
    )
  }

  test("asignacionOptimaPar: la asignacion encontrada tiene el costo reportado") {

    val (asig, costo) =
      asignacionOptimaPar(c1, a1, d1, w)

    assert(
      costoAsignacion(c1, a1, d1, asig, w) == costo
    )
  }

  test("asignacionOptimaPar: encuentra el mismo costo optimo que la version secuencial") {

    val (_, costoSec) =
      asignacionOptima(c1, a1, d1, w)

    val (_, costoPar) =
      asignacionOptimaPar(c1, a1, d1, w)

    assert(costoPar == costoSec)
  }

  test("asignacionOptimaPar: una instancia con un curso devuelve una asignacion de tamaño 1") {

    val cursos = Vector(
      ("M01", 4, 8, 25)
    )

    val (asig, _) =
      asignacionOptimaPar(cursos, a1, d1, w)

    assert(asig.length == 1)
  }

  test("asignacionOptimaPar: la asignacion obtenida pertenece al espacio generado") {

    val (asig, _) =
      asignacionOptimaPar(c1, a1, d1, w)

    val todas =
      generarAsignacionesPar(c1.length, a1.length)

    assert(todas.contains(asig))
  }



}
