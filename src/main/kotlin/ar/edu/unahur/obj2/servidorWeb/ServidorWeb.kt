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
  fun extención() = url.split(".").last()
}

class Respuesta(val codigo: CodigoHttp, val body: String, val tiempo: Int, val pedido: Pedido)


class Servidor {

  val modulos = mutableListOf<Modulo>()

  fun hayModuloQuePuedaResponder(unPedido: Pedido) = modulos.any { it.puedeResponderA(unPedido)}
  fun moduloQuePuedenResponder(unPedido: Pedido) = modulos.first { it.puedeResponderA(unPedido) }

  fun respuestaA(unPedido: Pedido) =
    if (hayModuloQuePuedaResponder(unPedido)) {
      moduloQuePuedenResponder(unPedido).generarRespuestaA(unPedido)
    } else {
      Respuesta(CodigoHttp.NOT_FOUND, "", 100, unPedido)
    }


  fun enviarRespuestaA(unPedido: Pedido) =
    if (unPedido.protocolo().equals("http")) {
      respuestaA(unPedido)
    } else {
      Respuesta(CodigoHttp.NOT_IMPLEMENTED, "", 100, unPedido)
    }
  }



class Modulo (val extenciones: MutableList<String>, val texto: String, val tiempo: Int ) {

  fun puedeResponderA (unPedido: Pedido) = extenciones.any { it.equals(unPedido.extención())}

  fun generarRespuestaA(unPedido: Pedido) = Respuesta(CodigoHttp.OK, texto, tiempo, unPedido)

}