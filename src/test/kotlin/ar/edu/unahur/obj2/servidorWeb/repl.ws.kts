import ar.edu.unahur.obj2.servidorWeb.Pedido
import java.time.LocalDateTime

val pedido1 = Pedido("192.168.110.11", "http://pepito.com.ar/documentos/doc1.html",
                     LocalDateTime.of(2020, 3, 20, 0, 0, 0))


pedido1.protocolo()

"https://Google.com.ar.html".split(":/").get(0)
"https://Google.com.ar.html".split(":/").get(1)
"https://Google.com.ar.html".split(".").last()