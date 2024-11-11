package thu.malva.ui.menu

import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import thu.malva.R
import thu.malva.databinding.FragmentMenuBinding

class MenuFragment : Fragment() {

    private var _binding: FragmentMenuBinding? = null
    private val binding get() = _binding!!
    private lateinit var menuViewModel: MenuViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMenuBinding.inflate(inflater, container, false)
        val root: View = binding.root

        menuViewModel = ViewModelProvider(this).get(MenuViewModel::class.java)

        val searchInput = root.findViewById<EditText>(R.id.search_input)
        val searchButton = root.findViewById<ImageButton>(R.id.search_btn)
        val tableLayout: TableLayout = root.findViewById(R.id.table_layout)

        menuViewModel.menuItems.observe(viewLifecycleOwner) { menuList ->
            filterMenu("", tableLayout, menuList)
        }

        searchButton.setOnClickListener {
            val query = searchInput.text.toString().trim().lowercase()
            filterMenu(query, tableLayout, menuViewModel.menuItems.value ?: listOf())
        }

        searchInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString().trim().lowercase()
                filterMenu(query, tableLayout, menuViewModel.menuItems.value ?: listOf())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        return root
    }

    private fun filterMenu(query: String, tableLayout: TableLayout, menuItems: List<MenuCategory>) {
        Log.d("MenuFragment", "Filtering menu with query: $query and items: $menuItems")
        tableLayout.removeAllViews()

        for (category in menuItems) {
            var hasItemsInCategory = false

            for (item in category.items) {
                if (item.name.lowercase().contains(query) || item.description.lowercase().contains(query)) {
                    if (!hasItemsInCategory) {
                        val categoryRow = TableRow(context)
                        val categoryName = TextView(context)
                        categoryName.text = category.name
                        categoryName.layoutParams = TableRow.LayoutParams(
                            TableRow.LayoutParams.MATCH_PARENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                        )
                        categoryName.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                        categoryName.textSize = 20f
                        categoryName.gravity = Gravity.START
                        categoryName.setPadding(0, 30, 0, 10)
                        categoryName.typeface = Typeface.create("serif", Typeface.BOLD)

                        categoryRow.addView(categoryName)
                        tableLayout.addView(categoryRow)

                        hasItemsInCategory = true
                    }

                    val tableRow = TableRow(context)
                    val dishName = TextView(context)
                    dishName.text = "${item.name}: ${item.description}"
                    dishName.layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 2f)
                    dishName.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                    dishName.textSize = 16f

                    val dishPrice = TextView(context)
                    dishPrice.text = item.price
                    dishPrice.layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)
                    dishPrice.gravity = Gravity.END
                    dishPrice.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                    dishPrice.textSize = 16f

                    tableRow.addView(dishName)
                    tableRow.addView(dishPrice)

                    tableLayout.addView(tableRow)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
