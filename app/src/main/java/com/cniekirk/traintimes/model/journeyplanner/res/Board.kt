package com.cniekirk.traintimes.model.journeyplanner.res
import com.squareup.moshi.Json

data class Board(
    @Json(name = "crsCode")
    val crsCode: String,
    @Json(name = "stationType")
    val stationType: String
)