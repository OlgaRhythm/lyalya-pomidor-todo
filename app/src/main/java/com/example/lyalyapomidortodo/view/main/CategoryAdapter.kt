import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.PopupMenu
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
    private val onCategoryClicked: (Category) -> Unit,
    private val onCategoryUpdated: (Category) -> Unit,
    private val onCategoryDeleted: (Category) -> Unit,
    private val onStartTimerClicked: (Category) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val txtCategoryTitle: TextView = itemView.findViewById(R.id.categoryTitle)
        private val btnCategoryOptions: ImageView = itemView.findViewById(R.id.btnCategoryOptions)

        fun bind(category: Category) {
            txtCategoryTitle.text = category.title
            // При нажатии на кнопку (например, в элементе разметки есть кнопка с id btnStartTimer) запускаем таймер
            itemView.findViewById<View>(R.id.btnStartTimer).setOnClickListener {
                onStartTimerClicked(category)
            }
            btnCategoryOptions.setOnClickListener { view ->
                showOptionsPopup(view, category)
            }
        }

        private fun showOptionsPopup(view: View, category: Category) {
            val popup = PopupMenu(view.context, view)
            popup.menuInflater.inflate(R.menu.category_options_menu, popup.menu)

            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.menu_edit -> {
                        showEditDialog(category)
                        true
                    }
                    R.id.menu_delete -> {
                        showDeleteDialog(category)
                        true
                    }
                    else -> false
                }
            }
            popup.show()
        }

        private fun showEditDialog(category: Category) {
            val dialogView = LayoutInflater.from(itemView.context)
                .inflate(R.layout.dialog_edit_category, null)

            val editText = dialogView.findViewById<EditText>(R.id.editTextCategoryName)
            val saveButton = dialogView.findViewById<Button>(R.id.saveButton)

            editText.setText(category.title)

            val dialog = AlertDialog.Builder(itemView.context)
                .setView(dialogView)
                .create()

            saveButton.setOnClickListener {
                val newTitle = editText.text.toString().trim()
                if (newTitle.isNotEmpty()) {
                    val updatedCategory = category.copy(title = newTitle)
                    onCategoryUpdated(updatedCategory)
                    dialog.dismiss()
                } else {
                    editText.error = "Введите название категории"
                }
            }

            dialog.show()
        }

        private fun showDeleteDialog(category: Category) {
            val dialogView = LayoutInflater.from(itemView.context)
                .inflate(R.layout.dialog_delete_category, null)

            val titleTextView = dialogView.findViewById<TextView>(R.id.titleTextView)
            val editButton = dialogView.findViewById<Button>(R.id.editButton)
            val deleteButton = dialogView.findViewById<Button>(R.id.deleteButton)

            titleTextView.text = "Удалить категорию '${category.title}'?"

            val dialog = AlertDialog.Builder(itemView.context)
                .setView(dialogView)
                .create()

            editButton.setOnClickListener {
                dialog.dismiss()
                showEditDialog(category)
            }

            deleteButton.setOnClickListener {
                onCategoryDeleted(category)
                dialog.dismiss()
            }

            dialog.show()
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