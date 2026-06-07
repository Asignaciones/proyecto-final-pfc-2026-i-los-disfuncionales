package proyecto

import org.scalatest.funsuite.AnyFunSuite
import org.junit.runner.RunWith
import org.scalatestplus.junit.JUnitRunner
import AsignacionAulas._

@RunWith(classOf[JUnitRunner])
class AsignacionAulasTest extends AnyFunSuite {

  // Ejemplo 1 del enunciado
  val c1: Cursos    = Vector(("M01", 4, 8, 25), ("M02", 6, 10, 30), ("M03", 12, 16, 20))
  val a1: Aulas     = Vector(("E101", 30), ("E102", 40))
  val d1: Distancias = Vector(Vector(0, 3), Vector(3, 0))
  val w: Pesos      = (1000, 100, 1, 2)

  // solapan
  test("solapan: M01[4,8) y M02[6,10) se solapan") {
    assert(solapan(("M01", 4, 8, 25), ("M02", 6, 10, 30)))
  }

  test("solapan: M01[4,8) y M03[12,16) no se solapan") {
    assert(!solapan(("M01", 4, 8, 25), ("M03", 12, 16, 20)))
  }

  test("solapan: cursos adyacentes [0,4) y [4,8) no se solapan") {
    assert(!solapan(("A", 0, 4, 10), ("B", 4, 8, 10)))
  }

  // test propios de solapan
  test("solapan: Curso contenido totalmente dentro de otro") {
    // M02 ocurre entre [6, 10). Un curso inclusivo [7, 9) debe solapar.
    assert(solapan(("M02", 6, 10, 30), ("INTERNO", 7, 9, 15)))
  }

  test("solapan: Mismo curso evaluado contra sí mismo") {
    // Un curso idéntico en tiempos lógicamente interseca consigo mismo.
    assert(solapan(("M01", 4, 8, 25), ("M01", 4, 8, 25)))
  }

  test("solapan: Cursos lejanos en el tiempo sin intersección") {
    // Un curso matutino temprano [0, 4) y uno nocturno [20, 22) no deben solapar.
    assert(!solapan(("TEMPRANO", 0, 4, 10), ("TARDE", 20, 22, 15)))
  }

  test("solapan: Caso de cruzamiento por el extremo izquierdo") {
    // Un curso [5, 9) inicia antes de que termine M02 [6, 10), provocando traslape.
    assert(solapan(("EXT_IZQ", 5, 9, 15), ("M02", 6, 10, 30)))
  }

  test("solapan: Caso de cruzamiento por el extremo derecho") {
    // Un curso [7, 11) inicia después de M02 [6, 10) pero antes de su finalización.
    assert(solapan(("M02", 6, 10, 30), ("EXT_DER", 7, 11, 20)))
  }

  // choques
  test("choques: asignacion [0,0,1] tiene 1 choque (M01 y M02 en E101)") {
    assert(choques(c1, Vector(0, 0, 1)) == 1)
  }

  test("choques: asignacion [0,1,0] no tiene choques") {
    assert(choques(c1, Vector(0, 1, 0)) == 0)
  }

  // test propios de choques
  test("choques: Todos los cursos asignados a la misma aula con conflicto") {
    // Vector(0, 0, 0) -> M01 y M02 chocan en el aula 0 (E101).
    // M03 no choca con nadie porque va de [12, 16). Total esperado = 1 choque.
    assert(choques(c1, Vector(0, 0, 0)) == 1)
  }

  test("choques: Cursos con horarios cruzados pero en aulas distintas") {
    // M01 y M02 se cruzan en tiempo, pero M01 va a E101 (0) y M02 va a E102 (1).
    // M03 va a E101 (0) pero no se cruza en tiempo con M01. Total esperado = 0 choques.
    assert(choques(c1, Vector(0, 1, 0)) == 0)
  }

  test("choques: Cursos no asignados (valores negativos) ignoran colisiones") {
    // Al usar -1, los cursos no tienen aula asignada, por lo que no generan conflictos válidos.
    assert(choques(c1, Vector(-1, -1, -1)) == 0)
  }

  test("choques: Asignación parcial donde solo una fracción de los cursos colisiona") {
    // Cuatro cursos donde los dos primeros se cruzan en el aula 0 y los otros no.
    val cCuatro = Vector(("C1", 8, 10, 10), ("C2", 9, 11, 10), ("C3", 13, 15, 10), ("C4", 16, 18, 10))
    assert(choques(cCuatro, Vector(0, 0, 1, 1)) == 1)
  }

  test("choques: Multiples cursos independientes en la misma aula sin intersección temporal") {
    // M01 [4,8) y M03 [12,16) están en el aula 0 (E101). Al no cruzarse en tiempo, el resultado es 0.
    assert(choques(c1, Vector(0, 1, 0)) == 0)
  }

  // capacidadFallida
  test("capacidadFallida: asignacion [0,0,1] no falla capacidad") {
    assert(capacidadFallida(c1, a1, Vector(0, 0, 1)) == 0)
  }

  // test propios de capacidadFallida
  test("capacidadFallida: Asignación donde un curso excede la capacidad del aula") {
    // Modificamos temporalmente el entorno para forzar un fallo.
    // Curso con 50 estudiantes en un aula con capacidad máxima de 30 (E101).
    val cFallo = Vector(("M99", 4, 8, 50))
    assert(capacidadFallida(cFallo, a1, Vector(0)) == 1)
  }

  test("capacidadFallida: Curso en el límite exacto de la capacidad del aula") {
    // M02 tiene 30 estudiantes y se asigna al aula E101 que tiene capacidad exacta de 30.
    // Al no ser estrictamente menor (30 < 30 es falso), no debe reportar fallo.
    val cLimite = Vector(("M02", 6, 10, 30))
    assert(capacidadFallida(cLimite, a1, Vector(0)) == 0)
  }

  test("capacidadFallida: Cursos sin aula asignada no cuentan como fallidos") {
    // Si un curso tiene asignación -1, se omite de la verificación física de espacio.
    val cGrande = Vector(("M99", 4, 8, 100))
    assert(capacidadFallida(cGrande, a1, Vector(-1)) == 0)
  }

  test("capacidadFallida: Multiples cursos excediendo simultáneamente el espacio asignado") {
    // Dos cursos que superan la capacidad límite de sus respectivas aulas.
    val cMultiFallo = Vector(("Fallo1", 8, 10, 35), ("Fallo2", 10, 12, 45))
    assert(capacidadFallida(cMultiFallo, a1, Vector(0, 1)) == 2)
  }

  test("capacidadFallida: Validación con un aula de gran capacidad que soporta cualquier curso") {
    // Aula masiva artificial para comprobar que el contador de fallos se mantiene en cero.
    val aMasiva = Vector(("AUDITORIO", 500))
    assert(capacidadFallida(c1, aMasiva, Vector(0, 0, 0)) == 0)
  }

  // desperdicio
  test("desperdicio: asignacion [0,0,1] tiene desperdicio 25") {
    // E101(30)-M01(25)=5, E101(30)-M02(30)=0, E102(40)-M03(20)=20 → 25
    assert(desperdicio(c1, a1, Vector(0, 0, 1)) == 25)
  }

  test("desperdicio: asignacion [0,1,0] tiene desperdicio 25") {
    // E101(30)-M01(25)=5, E102(40)-M02(30)=10, E101(30)-M03(20)=10 → 25
    assert(desperdicio(c1, a1, Vector(0, 1, 0)) == 25)
  }

  // costoAsignacion
  test("costoAsignacion: asignacion [0,0,1] cuesta 1031") {
    assert(costoAsignacion(c1, a1, d1, Vector(0, 0, 1), w) == 1031)
  }

  test("costoAsignacion: asignacion [0,1,0] cuesta 37") {
    assert(costoAsignacion(c1, a1, d1, Vector(0, 1, 0), w) == 37)
  }

  // generarAsignaciones
  test("generarAsignaciones: 2 cursos y 2 aulas produce 4 asignaciones") {
    assert(generarAsignaciones(2, 2).length == 4)
  }

  test("generarAsignaciones: 3 cursos y 3 aulas produce 27 asignaciones") {
    assert(generarAsignaciones(3, 3).length == 27)
  }

  // asignacionOptima
  test("asignacionOptima: el costo de la optima no supera el de [0,1,0] (37)") {
    val (_, costo) = asignacionOptima(c1, a1, d1, w)
    assert(costo <= 37)
  }
}
