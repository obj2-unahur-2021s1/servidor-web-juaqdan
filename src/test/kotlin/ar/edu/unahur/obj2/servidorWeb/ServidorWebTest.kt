package ar.edu.unahur.obj2.servidorWeb

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDateTime

class ServidorWebTest : DescribeSpec({
  val detectorDeDemoras = DetectorDeDemora(60)
  val ipSospechosa = IpsSospechosas(mutableListOf("192.168.110.11", "192.168.110.12","192.168.110.13"))
  val estadisticas = Estadistica()
  val moduloImg = Modulo(mutableListOf("png", "jpg", "gif"), "Imagen lista", 100 )
  val moduloDoc = Modulo(mutableListOf("txt", "doc", "docx"), "Documento listo", 50 )

  val pedido1 = Pedido("192.168.110.11", "http://google.com.ar.png",
                       LocalDateTime.of(2021,11,11, 10,12,12))
  val pedido2 = Pedido("192.168.110.12", "http://facebook.com.ar.jpg",
    LocalDateTime.of(2021,11,11, 10,12,12))

  val pedido3 = Pedido("192.168.110.13", "http://youtube.com.ar.txt",
    LocalDateTime.of(2021,11,11, 10,13,12))

  val pedido4 = Pedido("192.168.110.14", "https://homebanking.com.ar.txt",
    LocalDateTime.of(2021,11,11, 10,12,12))

  val pedido5 = Pedido("192.168.110.15", "http://oracle.com.ar.jar",
    LocalDateTime.of(2021,11,11, 10,12,12))

  val pedido6 = Pedido("192.168.110.13", "http://youtube.com.ar.png",
    LocalDateTime.of(2021,11,11, 10,12,12))

  val pedido7 = Pedido("192.168.110.12", "http://google.com.ar.png",
    LocalDateTime.of(2021,11,11, 10,12,12))



  describe ("Un pedido") {
    it("Protocolo") {
      pedido1.protocolo().shouldBe("http")
      pedido2.protocolo().shouldBe("http")
      pedido3.protocolo().shouldBe("http")
    }
    it ("Ruta") {
      pedido1.ruta().shouldBe("/google.com.ar.png")
      pedido2.ruta().shouldBe("/facebook.com.ar.jpg")
      pedido3.ruta().shouldBe("/youtube.com.ar.txt")
    }
    it ("Extención") {
      pedido1.extension().shouldBe("png")
      pedido2.extension().shouldBe("jpg")
      pedido3.extension().shouldBe("txt")
    }
  }

  describe ("Responder pedido") {

    it ("Protocolo incorrecto") {
      servidor.atenderPedido(pedido4).codigo.shouldBe(CodigoHttp.NOT_IMPLEMENTED)
    }
    it ("No hay modulo para responder") {
      servidor.atenderPedido(pedido2).codigo.shouldBe(CodigoHttp.NOT_FOUND)
    }
    it ("Consulta exitosa") {
      servidor.modulos.add(moduloImg)
      servidor.atenderPedido(pedido1).codigo.shouldBe(CodigoHttp.OK)
    }
  }

  describe( "Analizadores") {
    servidor.modulos.add(moduloImg)
    servidor.modulos.add(moduloDoc)
    servidor.analizadores.add(detectorDeDemoras)
    servidor.analizadores.add(ipSospechosa)

    servidor.atenderPedido(pedido1)
    servidor.atenderPedido(pedido3)

    describe("Tiempo de demora") {
      it("Modulo de imagen") {
        detectorDeDemoras.cantDeRespuestasDemoradasDe(moduloImg).shouldBe(1)
      }
      it("Modulo de documento") {
        detectorDeDemoras.cantDeRespuestasDemoradasDe(moduloImg).shouldBe(0)
      }
    }

    describe("Ips sospechosas") {
      describe("Cantidad de pedidodos de una ip sospechosa") {
        it ("192.168.110.11") {
          ipSospechosa.cantPedidosConIpSospechosaDe("192.168.110.11").shouldBe(1)
        }
        it ("192.168.110.13") {
          servidor.atenderPedido(pedido6)
          ipSospechosa.cantPedidosConIpSospechosaDe("192.168.110.13").shouldBe(2)
        }
      }
    }
    describe( "Conjunto de IPs sospechosas que requirieron una cierta ruta") {
      servidor.atenderPedido(pedido7)
      it ("hola") {
        ipSospechosa.ipsSospechosasDeUnaruta("/google.com.ar.png").shouldBe(listOf("192.168.110.11","192.168.110.12"))
      }
    }
  }
  describe("Modulo con mas iconsultas sospechosas") {
    servidor.modulos.add(moduloImg)
    servidor.modulos.add(moduloDoc)
    servidor.analizadores.add(detectorDeDemoras)
    servidor.analizadores.add(ipSospechosa)

    servidor.atenderPedido(pedido1)
    servidor.atenderPedido(pedido3)
    it ("Modulo de imagen") {
      ipSospechosa.moduloConMasConsultasSospechosas().shouldBe(moduloImg)
    }
  }

  describe( "Estadisticas") {
    servidor.modulos.add(moduloImg)
    servidor.modulos.add(moduloDoc)
    servidor.analizadores.add(detectorDeDemoras)
    servidor.analizadores.add(ipSospechosa)
    servidor.analizadores.add(estadisticas)

    servidor.atenderPedido(pedido1)
    servidor.atenderPedido(pedido2)
    servidor.atenderPedido(pedido3)


    it ("Tiempo promedio") {
      estadisticas.tiempoPromedioDeRespuesta().shouldBe(83)
    }

    it ("Pedidos entres dos fechas") {
      estadisticas.cantPedidosEntreFechas(LocalDateTime.of(2021,11,11, 0,0,0),
        LocalDateTime.of(2021,11,14, 0,0,0,0)).shouldBe(3)
    }
    it ("cantidad de respuestas cuyo body incluye un determinado string") {
      estadisticas.cantRespuestasQueConstienen("Imagen").shouldBe(2)
    }
    it ("Porcentaje de respuesta exitosa") {
      estadisticas.porecentajePedidosConRespuestaExitosa().shouldBe(100.0)
      servidor.atenderPedido(pedido4)
      estadisticas.porecentajePedidosConRespuestaExitosa().shouldBe(75.0)
    }
  }
})
