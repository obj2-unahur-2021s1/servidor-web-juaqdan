package ar.edu.unahur.obj2.servidorWeb

import java.time.LocalDateTime

// Para no tener los códigos "tirados por ahí", usamos un enum que le da el nombre que corresponde a cada código
// La idea de las clases enumeradas es usar directamente sus objetos: CodigoHTTP.OK, CodigoHTTP.NOT_IMPLEMENTED, etc
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

  fun respuestaA(unPedido: Pedido) =
    if (hayModuloQuePuedaResponder(unPedido)) {
      moduloQuePuedenResponder(unPedido).generarRespuestaA(unPedido)
    } else {
      Respuesta(CodigoHttp.NOT_FOUND, "", 10, unPedido, null)
    }


  fun atenderPedido(unPedido: Pedido) =
    if (unPedido.protocolo().equals("http")) {
      respuestaA(unPedido)
    } else {
      Respuesta(CodigoHttp.NOT_IMPLEMENTED, "", 10, unPedido, null)
    }

}





class Modulo (val extenciones: MutableList<String>, val texto: String, val tiempo: Int ) {

  val respuestas = mutableListOf<Respuesta>()
  val pedidos = mutableListOf<Pedido>()

  fun puedeResponderA (unPedido: Pedido) = extenciones.any { it.equals(unPedido.extension())}

  fun generarRespuestaA(unPedido: Pedido): Respuesta {
    val respuesta: Respuesta
    respuesta = Respuesta(CodigoHttp.OK, texto, tiempo, unPedido, this)
    respuestas.add(respuesta)
    pedidos.add(unPedido)
    return respuesta
  }

}

abstract class Analizador {

}

class DetectorDeDemora (val demoraMinima: Int): Analizador() {

   fun cantDeRespuestasDemoradasDe(unModulo: Modulo) = unModulo.respuestas.count { it.tiempo > demoraMinima }

}

class IpsSospechosas (val listaDeSospecha: MutableList<String>): Analizador() {

  val respuestas = mutableListOf<Respuesta>()

  val pedidosSospechosos = mutableListOf<Pedido>()

  fun pedidosConIpSospechosa(unaIp: String) = pedidosSospechosos.count { it.ip == unaIp }

  fun cantPedidosConIpSospechosaDe (unModulo: Modulo) = unModulo.pedidos.count { listaDeSospecha.contains(it.ip) }

  fun moduloConMasPedidosSospechosos() = servidor.modulos.maxByOrNull { cantPedidosConIpSospechosaDe(it) }

  //fun ipsSospechosasDeUnaruta (unaRuta: String) =


}

