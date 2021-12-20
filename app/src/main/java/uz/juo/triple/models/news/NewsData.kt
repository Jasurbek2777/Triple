package uz.juo.triple.models.news

data class NewsData(
    val `data`: List<Data>,
    val pagination: Pagination
)