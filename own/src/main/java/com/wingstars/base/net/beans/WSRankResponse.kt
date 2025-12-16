package com.wingstars.base.net.beans


data class WSRankResponse(
    val id: Int,
    val title: Title,                   //标题
    val content: Content,               //內文
    val acf: Acf,                       //排行
) {
    val titleF: String                  //title format
        get() {
            return title.rendered
        }

    val contentF: String                //content format
        get() {
            return content.rendered
        }

    data class Title(
        val rendered: String,           //圖示
    ) : java.io.Serializable

    data class Content(
        val rendered: String,           //資訊
    ) : java.io.Serializable

    data class Acf(
        val first: RankBean,            //第一名
        val second: RankBean,           //第二名
        val third: RankBean,            //第三名
        val four: RankBean,             //第四名
        val five: RankBean,             //第五名
        val six: RankBean,              //第六名
        val seven: RankBean,            //第七名
        val eight: RankBean,            //第八名
        val nine: RankBean,             //第九名
        val ten: RankBean,              //第十名
    ) : java.io.Serializable {
        fun rankBean(index: Int): RankBean?   {           //first ~ ten format
            return when(index){
                1 -> first
                2 -> second
                3 -> third
                4 -> four
                5 -> five
                6 -> six
                7 -> seven
                8 -> eight
                9 -> nine
                10 -> ten
                else -> null
            }
        }

        data class RankBean(
            val name: String,           //名字
            val volume: String,         //聲量
        ) : java.io.Serializable
    }
}
