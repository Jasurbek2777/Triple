package uz.juo.triple.models.news

data class Pagination(
    val count: Int,
    val limit: Int,
    val offset: Int,
    val total: Int
)