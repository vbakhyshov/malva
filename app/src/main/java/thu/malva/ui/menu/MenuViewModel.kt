package thu.malva.ui.menu

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MenuViewModel : ViewModel() {

    private val _menuItems = MutableLiveData<List<MenuCategory>>()
    val menuItems: LiveData<List<MenuCategory>> = _menuItems

    init {
        fetchMenuData()
    }

    private fun fetchMenuData() {
        val database = FirebaseDatabase.getInstance().getReference("menu")
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val menuList = mutableListOf<MenuCategory>()
                for (categorySnapshot in dataSnapshot.children) {
                    val category = categorySnapshot.getValue(MenuCategory::class.java)
                    if (category != null) {
                        menuList.add(category)
                    }
                }
                _menuItems.value = menuList
                Log.d("MenuViewModel", "Fetched menu data: $menuList")
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("MenuViewModel", "Database error: ${databaseError.message}")
            }
        })
    }
}
