object PlayingField {
    object Size {
        var width: Int = 0
        var height: Int = 0
    }
    fun changeSize(newWidth: Int, newHeight: Int) = Size.apply {
        width = newWidth
        height = newHeight
    }
}