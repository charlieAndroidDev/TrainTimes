package com.cniekirk.traintimes.model.getdepboard.req

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "v:Body")
data class ArrBody(
    @Element val getArrBoardWithDetailsRequest: GetArrBoardWithDetailsRequest
)