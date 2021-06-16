package ar.edu.unahur.obj2.servidorWeb

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDateTime

class ServidorWebTest : DescribeSpec({

  val pedido1 = Pedido("192.168.110.11", "http://pepito.com.ar/documentos/doc1.html",
    LocalDateTime.of(2020, 3, 20, 0, 0, 0))

  val pedido2 = Pedido("192.168.110.12", "https://pepito.com.ar/documentos/doc2.html",
    LocalDateTime.of(2020, 3, 20, 0, 0, 0))

  describe ( "Un pedido") {
    it ("Protocolo") {
      pedido1.protocolo().shouldBe("http")
    }
    it ("Ruta") {
      pedido1.ruta().shouldBe("/pepito.com.ar/documentos/doc1.html")
    }
    it ("Extención") {
      pedido1.extención().shouldBe("html")
    }
  }

  describe("Un servidor web") {
    val servidor1 = Servidor()
    it ("Recibir pedido") {
      servidor1.enviarRespuestaA(pedido2).codigo.codigo.shouldBe(501)
    }
  }
})
