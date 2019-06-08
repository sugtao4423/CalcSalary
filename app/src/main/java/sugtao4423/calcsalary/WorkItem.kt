package sugtao4423.calcsalary

data class WorkItem(
        val rowId: Int,
        val jikyu: Int,
        val hour: Int,
        val minute: Int,
        val changed: String,
        val memo: String
)
