package www.edu.udb.sv

import com.google.firebase.Timestamp
import java.util.Date

data class Gasto (

    /* Clase de datos para representar un Gasto.*/

        // ID del documento en Firestore (se guarda explícitamente después de la creación)
        var id: String? = null,
        val nombre: String = "",
        val monto: Double = 0.0,
        val categoria: String = "",
        // Usamos Timestamp de Firebase para facilitar las consultas de rango
        val fecha: Timestamp? = null,
        // ID del usuario para asociar el gasto. ¡Clave para la seguridad!
        val userId: String = ""
    ) {
        // Constructor vacío necesario para el mapeo (toObject) de Firestore
        constructor() : this(null, "", 0.0, "", null, "")

        /**
         * Función helper para obtener la fecha como objeto Date de Java.
         */
        fun getDate(): Date? {
            return fecha?.toDate()
        }
    }
