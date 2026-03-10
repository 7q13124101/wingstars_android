package com.wingstars.base.net.beans

data class WSCalendarNResponse(
    val id: Int,
    val title: String,
    val content: String,
    val start_date: String,
    val end_date: String,
    val location: String,
    val precautions: String,
    val image_url: String,
    val category: Int
) :java.io.Serializable {
    val titleF: String                      //title format
        get() {
            return title
        }

    val contentF: String                    //content format
        get() {
            return content
        }

    val categoryF: Int             //category format
        get() {
            return category
        }

    val st_dateF: String                //start_date format
        get() {
            return if (start_date.length >= 10) {
                start_date.substring(0, 16)
            } else {
                start_date
            }
        }

    val ed_dateF: String                    //end_date format
        get() {
            return end_date
        }
    val contentRaw: String
        get() = content
    val locationF: String                        //location format
        get() {
            return location
        }

    val precautionsF: String                //precautions format
        get() {
            return precautions
        }

    val urlF: String                        //image_url format
        get() {
            return image_url
        }
}
