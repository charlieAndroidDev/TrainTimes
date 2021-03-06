package com.cniekirk.traintimes.model.journeyplanner.res


import com.cniekirk.traintimes.model.adapter.SingleToArray
import com.squareup.moshi.Json

data class Leg(
    @Json(name = "alight")
    val alight: Alight?,
    @Json(name = "board")
    val board: Board?,
    @Json(name = "cateringCodes")
    val cateringCodes: String?,
    @SingleToArray
    @Json(name = "destinationInstants")
    val destinationInstants: List<DestinationInstants>?,
    @Json(name = "destinationPlatform")
    val destinationPlatform: String?,
    @SingleToArray
    @Json(name = "destinations")
    val destinations: List<String>?,
    @Json(name = "id")
    val id: String?,
    @Json(name = "iptisTripIdentifier")
    val iptisTripIdentifier: String?,
    @Json(name = "isReplacementBus")
    val isReplacementBus: String?,
    @Json(name = "mode")
    val mode: String?,
    @Json(name = "operator")
    val `operator`: Operator?,
    @field:SingleToArray
    @Json(name = "originInstants")
    val originInstants: List<OriginInstants>?,
    @Json(name = "originPlatform")
    val originPlatform: String?,
    @Json(name = "origins")
    val origins: String?,
    @Json(name = "realtimeClassification")
    val realtimeClassification: String?,
    @Json(name = "reservable")
    val reservable: String?,
    @Json(name = "seatingClass")
    val seatingClass: String?,
    @Json(name = "temporaryTrain")
    val temporaryTrain: String?,
    @Json(name = "timetable")
    val timetable: Timetable?,
    @Json(name = "trainCategory")
    val trainCategory: String?,
    @Json(name = "trainRetailID")
    val trainRetailID: String?,
    @Json(name = "trainUID")
    val trainUID: String?,
    @Json(name = "undergroundTravelInformation")
    val undergroundTravelInfo: UndergroundTravelInfo?
)