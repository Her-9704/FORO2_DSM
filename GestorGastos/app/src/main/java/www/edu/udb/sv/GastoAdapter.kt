package www.edu.udb.sv

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import www.edu.udb.sv.R
import www.edu.udb.sv.Gasto
import java.text.SimpleDateFormat
import java.util.Locale

class GastoAdapter(private var gastos: List<Gasto>) :
    RecyclerView.Adapter<GastoAdapter.GastoViewHolder>() {

    // Formato de fecha para mostrar al usuario
    private val dateFormat = SimpleDateFormat("dd/MMM/yyyy", Locale.getDefault())

    /**
     * Define y enlaza las vistas dentro de cada elemento de la lista.
     */
    class GastoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNombre: TextView = view.findViewById(R.id.tv_nombre_gasto)
        val tvMonto: TextView = view.findViewById(R.id.tv_monto_gasto)
        val tvDetalle: TextView = view.findViewById(R.id.tv_detalle_gasto)
    }

    /**
    carga el layout del elemento de la lista.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GastoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_gasto, parent, false)
        return GastoViewHolder(view)
    }

    /**
     * Asigna los datos del objeto Gasto a las vistas del ViewHolder.
     */
    override fun onBindViewHolder(holder: GastoViewHolder, position: Int) {
        val gasto = gastos[position]

        holder.tvNombre.text = gasto.nombre
        // Formatea el monto para mostrar dos decimales
        holder.tvMonto.text = String.format("$ %.2f", gasto.monto)

        // Formatea la fecha
        val fechaStr = gasto.getDate()?.let { dateFormat.format(it) } ?: "N/A"

        // Muestra la categoría y la fecha
        holder.tvDetalle.text = "${gasto.categoria} | $fechaStr"

    }

    override fun getItemCount(): Int = gastos.size

    /**
     * Función pública para actualizar la lista de gastos y notificar al RecyclerView.
     * Esta función se llama desde GastosActivity.kt después de obtener los datos de Firestore.
     */
    fun updateData(newGastos: List<Gasto>) {
        gastos = newGastos
        notifyDataSetChanged() // Notifica al RecyclerView que los datos han cambiado
    }
}