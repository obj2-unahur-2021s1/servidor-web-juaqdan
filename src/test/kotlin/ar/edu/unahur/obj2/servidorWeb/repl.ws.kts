import io.mockk.InternalPlatformDsl.toStr
import io.mockk.internalSubstitute
import org.apache.commons.io.filefilter.TrueFileFilter

// Pueden usar este archivo para hacer pruebas rápidas,
// de la misma forma en que usaban el REPL de Wollok.

// OJO: lo que esté aquí no será tenido en cuenta
// en la corrección ni reemplaza a los tests.

"https://Google.com.ar.html".split(":/").get(0)
"https://Google.com.ar.html".split(":/").get(1)
"https://Google.com.ar.html".split(".").last()