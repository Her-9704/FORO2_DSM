package www.edu.udb.sv

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import www.edu.udb.sv.GastosManagement
import www.edu.udb.sv.Gasto
import www.edu.udb.sv.GastoAdapter
import www.edu.udb.sv.R
import java.util.Calendar
import java.util.Date


class GastosActivity : AppCompatActivity() {

    // Instancia del Manager para la lógica de Firestore
    private val gastoManager = GastosManagement()

    private lateinit var gastoAdapter: GastoAdapter

    // Declaración de Vistas
    private lateinit var etNombre: EditText
    private lateinit var etMonto: EditText
    private lateinit var etCategoria: EditText
    private lateinit var btnGuardar: Button
    private lateinit var tvTotalMensual: TextView
    private lateinit var rvHistorial: RecyclerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_gastos)

        // Inicialización y enlace de vistas
        inicializarVistas()

        // Configuración del RecyclerView
        configurarRecyclerView()

        btnGuardar.setOnClickListener {
            ingresarNuevoGasto()
        }

        cargarDatos()
    }

    private fun inicializarVistas() {
        // Asigna los objetos de Kotlin a las vistas encontradas por su ID
        etNombre = findViewById(R.id.et_nombre)
        etMonto = findViewById(R.id.et_monto)
        etCategoria = findViewById(R.id.et_categoria)
        btnGuardar = findViewById(R.id.btn_guardar)
        tvTotalMensual = findViewById(R.id.tv_total_mensual)
        rvHistorial = findViewById(R.id.rv_historial)
    }

    private fun configurarRecyclerView() {
        // Inicializa el adaptador
        gastoAdapter = GastoAdapter(emptyList())

        rvHistorial.apply {
            layoutManager = LinearLayoutManager(this@GastosActivity)
            adapter = gastoAdapter
        }
    }

    //---------------------------------------------------------
    // 1. Ingresar nuevos gastos y guardar en Firestore
    //---------------------------------------------------------
    private fun ingresarNuevoGasto() {
        val nombre = etNombre.text.toString().trim()
        val montoStr = etMonto.text.toString().trim()
        val categoria = etCategoria.text.toString().trim()
        val fecha = Date() // Fecha y hora actuales

        val monto = montoStr.toDoubleOrNull()
        if (nombre.isEmpty() || monto == null || monto <= 0 || categoria.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos válidos.", Toast.LENGTH_SHORT).show()
            return
        }

        // Usamos gastoManager.guardarGasto
        gastoManager.guardarGasto(nombre, monto, categoria, fecha,
            onSuccess = {
                Toast.makeText(this, "✅ Gasto registrado.", Toast.LENGTH_SHORT).show()
                etNombre.text.clear()
                etMonto.text.clear()
                etCategoria.text.clear()
                cargarDatos()
            },
            onFailure = { e ->
                Toast.makeText(this, "❌ Error al guardar: ${e.message}", Toast.LENGTH_LONG).show()
            }
        )
    }

    //---------------------------------------------------------
    // 2. Ver Historial y 3. Calcular Total
    //---------------------------------------------------------
    private fun cargarDatos() {
        // Cargar Historial (para el RecyclerView)
        gastoManager.obtenerHistorialGastos { listaGastos ->
            gastoAdapter.updateData(listaGastos)
        }

        // Calcular y mostrar el total mensual
        val cal = Calendar.getInstance()
        val mesActual = cal.get(Calendar.MONTH) + 1
        val anioActual = cal.get(Calendar.YEAR)

        gastoManager.obtenerGastosMensuales(mesActual, anioActual) { gastosDelMes ->
            val total = gastosDelMes.sumOf { it.monto }
            tvTotalMensual.text = String.format("TOTAL MENSUAL: $%.2f", total)
        }
    }
}