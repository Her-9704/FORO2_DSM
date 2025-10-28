package www.edu.udb.sv

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import www.edu.udb.sv.Gasto
import java.util.Calendar
import java.util.Date


class GastosManagement {


        private val db = FirebaseFirestore.getInstance()
        private val auth = FirebaseAuth.getInstance()

        // Propiedad segura para obtener el ID del usuario actual
        private val currentUserId: String?
            get() = auth.currentUser?.uid

        private val GASTOS_COLLECTION = "gastos"

        /**
         * Guarda un nuevo gasto en Firestore.
         * @param fecha Usamos java.util.Date que se convierte a Timestamp para Firestore.
         */
        fun guardarGasto(nombre: String, monto: Double, categoria: String, fecha: Date,
                         onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {

            val userId = currentUserId
            if (userId == null) {
                onFailure(Exception("Error: Usuario no autenticado."))
                return
            }

            val nuevoGasto = Gasto(
                nombre = nombre,
                monto = monto,
                categoria = categoria,
                fecha = Timestamp(fecha), // Convertir a Timestamp
                userId = userId
            )

            db.collection(GASTOS_COLLECTION)
                .add(nuevoGasto)
                .addOnSuccessListener { documentReference ->
                    // Guardar el ID generado por Firestore en el propio documento
                    db.collection(GASTOS_COLLECTION).document(documentReference.id).update("id", documentReference.id)
                    onSuccess(documentReference.id)
                }
                .addOnFailureListener { e ->
                    onFailure(e)
                }
        }

        /**
         * Consulta 1: Obtiene el historial de gastos del usuario, ordenado por fecha.
         */
        fun obtenerHistorialGastos(onResult: (List<Gasto>) -> Unit) {
            val userId = currentUserId
            if (userId == null) {
                onResult(emptyList())
                return
            }

            db.collection(GASTOS_COLLECTION)
                .whereEqualTo("userId", userId) // Filtro por usuario autenticado
                .orderBy("fecha", Query.Direction.DESCENDING) // Ordenar para historial
                .get()
                .addOnSuccessListener { result ->
                    val listaGastos = result.toObjects(Gasto::class.java)
                    onResult(listaGastos)
                }
                .addOnFailureListener { exception ->
                    println("Error al obtener historial de gastos: $exception")
                    onResult(emptyList())
                }
        }

        /**
         * Consulta 2: Obtiene los gastos del usuario para un mes específico.
         * Esto permite el cálculo del total mensual.
         */
        fun obtenerGastosMensuales(mes: Int, anio: Int, onResult: (List<Gasto>) -> Unit) {
            val userId = currentUserId
            if (userId == null) {
                onResult(emptyList())
                return
            }

            // 1. Configurar el inicio del mes
            val calInicio = Calendar.getInstance().apply {
                set(anio, mes - 1, 1, 0, 0, 0) // Meses en Calendar van de 0-11
                set(Calendar.MILLISECOND, 0)
            }

            // 2. Configurar el fin del mes
            val calFin = Calendar.getInstance().apply {
                set(anio, mes - 1, getActualMaximum(Calendar.DAY_OF_MONTH), 23, 59, 59)
                set(Calendar.MILLISECOND, 999)
            }

            val inicioMes = Timestamp(calInicio.time)
            val finMes = Timestamp(calFin.time)

            db.collection(GASTOS_COLLECTION)
                .whereEqualTo("userId", userId)
                .whereGreaterThanOrEqualTo("fecha", inicioMes) // Rango de inicio
                .whereLessThanOrEqualTo("fecha", finMes) // Rango de fin
                .get()
                .addOnSuccessListener { result ->
                    val gastosMes = result.toObjects(Gasto::class.java)
                    onResult(gastosMes)
                }
                .addOnFailureListener { e ->
                    println("Error al obtener gastos mensuales: $e")
                    onResult(emptyList())
                }
        }

        /**
         * Consulta 3 (Opcional): Filtrar por Categoría.
         */
        fun filtrarPorCategoria(categoria: String, onResult: (List<Gasto>) -> Unit) {
            val userId = currentUserId
            if (userId == null) {
                onResult(emptyList())
                return
            }

            db.collection(GASTOS_COLLECTION)
                .whereEqualTo("userId", userId)
                .whereEqualTo("categoria", categoria) // Filtro de categoría
                .orderBy("fecha", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener { result ->
                    onResult(result.toObjects(Gasto::class.java))
                }
                .addOnFailureListener { e ->
                    println("Error al filtrar por categoría: $e")
                    onResult(emptyList())
                }
        }
    }
