package ar.edu.unahur.obj2.servidorWeb

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDateTime

class ServidorWebTest : DescribeSpec({

  val pedido1 = Pedido("192.168.110.11", "http://pepito.com.ar/documentos/doc1.html",
    LocalDateTime.of(2020, 3, 20, 0, 0, 0))

  val pedido2 = Pedido("192.168.110.12", "https://pepito.com.ar/documentos/doc2.html",
    LocalDateTime.of(2020, 3, 20, 0, 0, 0))

  val pedido3 = Pedido("192.168.110.13", "http://pepito.com.ar/imagenes/img1.png",
    LocalDateTime.of(2020, 3, 20, 5, 30, 0))

  val moduloDocumentos = Modulo(mutableListOf<String>("docx", "pdf", "html"), "Documento listo", 3)
  servidor.modulos.add(moduloDocumentos)

  val moduloImagenes = Modulo(mutableListOf<String>("gif", "png", "jpg"), "Imagen lista", 7)
  servidor.modulos.add(moduloImagenes)

  val ipSospechosas = IpsSospechosas(mutableListOf<String>("192.168.110.13", "192.168.110.11"))
  servidor.analizadores.add(ipSospechosas)

  val detectorDeDemoras = DetectorDeDemora(2)
  servidor.analizadores.add(detectorDeDemoras)

  describe ( "Un pedido") {
    it ("Protocolo") {
      pedido1.protocolo().shouldBe("http")
    }
    it ("Ruta") {
      pedido1.ruta().shouldBe("/pepito.com.ar/documentos/doc1.html")
    }
    it ("Extención") {
      pedido1.extension().shouldBe("html")
    }
  }

  describe("Un servidor web") {
    it ("Atender pedido") {
      servidor.atenderPedido(pedido2).codigo.codigo.shouldBe(501)
      servidor.atenderPedido(pedido3).codigo.codigo.shouldBe(200)
    }
  }
    describe("analizadores") {

        it("detector de demoras") {
          detectorDeDemoras.cantDeRespuestasDemoradasDe(moduloImagenes).shouldBe(0)
        }
      it("ip sospechosas"){
        ipSospechosas.cantPedidosConIpSospechosaDe("192.168.110.13").shouldBe(0)
      }
    }
})
