package my.client.dgsv

//creating a Data Model Class
data class LocalQuetions(
    var Question: String = "",
    var Option1: String = "",
    var Option2: String = "",
    var Option3: String = "",
    var Option4: String = "",
    var Answer: Int = 0,
    val userId: Int
)