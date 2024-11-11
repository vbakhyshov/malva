package thu.malva.ui.menu

data class MenuCategory(
    val name: String = "",
    val items: List<MenuItem> = listOf()
)

data class MenuItem(
    val name: String = "",
    val description: String = "",
    val price: String = ""
)
