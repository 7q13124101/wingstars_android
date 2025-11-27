package com.wingstars.user.frequentlyaskedquestion

data class FrequentlyQuestionsResponse(
    val code: Int,
    val data: List<Data>,
    val message: String
) {
    data class Data(
        val partName: String,
        val outData: List<GroupDto>
    ){
        data class GroupDto(
            val topTitle: String,
            val insideData: List<ItemDto>
        ) {
            data class ItemDto
                (
                val titleNum: String,
                val title: String,
                val content: List<String>,
                var isExpanded:Boolean = false
            )
        }
    }
}