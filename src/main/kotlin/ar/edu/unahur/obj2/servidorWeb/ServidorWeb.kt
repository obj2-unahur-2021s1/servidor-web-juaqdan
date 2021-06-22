package ar.edu.unahur.obj2.servidorWeb

import java.security.interfaces.RSAKey
import java.time.LocalDate
import java.time.LocalDateTime

enum class CodigoHttp(val codigo: Int) {
  OK(200),
  NOT_IMPLEMENTED(501),
  NOT_FOUND(404),
}

class Pedido(val ip: String, val url: String, val fechaHora: LocalDateTime) {
  fun protocolo() = url.split(":/").get(0)
  fun ruta() = url.split(":/").get(1)
  fun extension() = url.split(".").last()
}

class Respuesta(val codigo: CodigoHttp, val body: String, val tiempo: Int, val pedido: Pedido, val modulo: Modulo?)


object servidor {

  val modulos = mutableListOf<Modulo>()
  val analizadores = mutableListOf<Analizador>()

  fun hayModuloQuePuedaResponder(unPedido: Pedido) = modulos.any { it.puedeResponderA(unPedido)}
  fun moduloQuePuedenResponder(unPedido: Pedido) = modulos.first { it.puedeResponderA(unPedido) }
  fun enviarRespuestaAnalizadores(unaRespuesta : Respuesta) {
      if (analizadores.size > 0) analizadores.forEach { it.respuestas.add(unaRespuesta) }
  }

  fun respuestaA(unPedido: Pedido) =
    if (hayModuloQuePuedaResponder(unPedido) && unPedido.protocolo().equals("http")) {
      moduloQuePuedenResponder(unPedido).generarRespuestaA(unPedido)
    } else {
      respuestaDeError(unPedido)
    }

  fun respuestaDeError(unPedido: Pedido) =
      noModulo.generarRespuestaA(unPedido)


  fun atenderPedido(unPedido: Pedido) : Respuesta{
    val respuesta = respuestaA(unPedido)
    enviarRespuestaAnalizadores(respuesta as Respuesta)
    return(respuesta)
  }

}





open class Modulo (val extenciones: MutableList<String>, val texto: String, val tiempo: Int ) {

  val respuestas = mutableListOf<Respuesta>()
  val pedidos = mutableListOf<Pedido>()

  fun puedeResponderA (unPedido: Pedido) = extenciones.any { it.equals(unPedido.extension())}

  open fun generarRespuestaA(unPedido: Pedido): Respuesta {
    val respuesta: Respuesta
    respuesta = Respuesta(CodigoHttp.OK, texto, tiempo, unPedido, this)
    respuestas.add(respuesta)
    pedidos.add(unPedido)
    return respuesta
  }

}

object noModulo: Modulo(extenciones = mutableListOf(), texto = "", tiempo = 100) {
  override fun generarRespuestaA(unPedido: Pedido): Respuesta {
    val respuesta =
      if (unPedido.protocolo().equals("http")) {
        Respuesta(CodigoHttp.NOT_FOUND, texto, tiempo, unPedido, this)
      } else {
        Respuesta(CodigoHttp.NOT_IMPLEMENTED, texto, tiempo, unPedido, this)
      }

    respuestas.add(respuesta)
    pedidos.add(unPedido)
    return respuesta
  }
}

abstract class Analizador {
  val respuestas = mutableListOf<Respuesta>()

}

class DetectorDeDemora (val demoraMinima: Int): Analizador() {

   fun cantDeRespuestasDemoradasDe(unModulo: Modulo) = unModulo.respuestas.count { it.tiempo > demoraMinima }

}

class IpsSospechosas (val listaDeSospecha: MutableList<String>): Analizador() {

  val pedidosSospechosos = mutableListOf<Pedido>()

  fun cantPedidosConIpSospechosaDe (unaIp: String) = respuestas.count { it.pedido.ip == unaIp }

  fun modulosRegistrados() = respuestas.map {it.modulo}

  fun consultasSospechosasA(unModulo: Modulo) = unModulo.pedidos.count { listaDeSospecha.contains(it.ip) }

  fun moduloConMasPedidosSospechosos() =
    if (modulosRegistrados().size > 0) {
      modulosRegistrados().maxBy { consultasSospechosasA(it!!) }
    } else {
      noModulo
    }
  fun ipsSospechosasDeUnaruta (unaRuta: String) = pedidosSospechosos.filter { it.ruta().equals(unaRuta) }.map { it.ip }

}

class Estadistica: Analizador() {
  fun tiempoPromedioDeRespuesta() = if(respuestas.size > 0) respuestas.sumBy { it.tiempo } / respuestas.size else 0

  fun pedidos() = respuestas.map { it.pedido }
  fun cantPedidosEntreFechas(fechaInicio: LocalDateTime, fechaFin: LocalDateTime) =
    pedidos().count { it.fechaHora in fechaInicio..fechaFin }

  fun cantRespuestasQueConstienen(unaPalabra: String) = respuestas.count { it.body.contains(unaPalabra) }

  fun cantDeRespuestasExitosas() = respuestas.count { it.codigo == CodigoHttp.OK }
  fun porecentajePedidosConRespuestaExitosa() = if(respuestas.size > 0) (cantDeRespuestasExitosas() / respuestas.size) * 100 else 0

}