package uz.juo.triple.models.azon

data class Method(
    val id: Int,
    val location: Location,
    val name: String,
    val params: Params
)