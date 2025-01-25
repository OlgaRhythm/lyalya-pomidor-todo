import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lyalyapomidortodo.R
import com.example.lyalyapomidortodo.data.local.entities.Category
import com.example.lyalyapomidortodo.data.local.entities.Task
import com.example.lyalyapomidortodo.view.main.CategoryListAdapter
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer

class CategoryAdapter(
    private var categories: List<Category>,
    private val onStartTimerClicked: (Category) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val txtCategoryTitle: TextView = itemView.findViewById(R.id.categoryTitle)
        fun bind(category: Category) {
            txtCategoryTitle.text = category.title
            // При нажатии на кнопку (например, в элементе разметки есть кнопка с id btnStartTimer) запускаем таймер
            itemView.findViewById<View>(R.id.btnStartTimer).setOnClickListener {
                onStartTimerClicked(category)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(categories[position])
    }

    override fun getItemCount(): Int = categories.size

    fun updateData(newCategories: List<Category>) {
        categories = newCategories
        notifyDataSetChanged()
    }
}