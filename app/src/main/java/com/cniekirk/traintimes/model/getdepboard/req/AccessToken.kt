package com.cniekirk.traintimes.model.getdepboard.req

import com.tickaroo.tikxml.annotation.Attribute
import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "typ:AccessToken")
data class AccessToken(
    //@Attribute(name = "xmlns:n0") val xmlns: String = "http://thalesgroup.com/RTTI/2013-11-28/Token/types",
    @PropertyElement(name = "typ:TokenValue") val tokenValue: String = "75a81e5a-122e-44a2-ae01-3d2266585f70"//"d6fe5dae-7c49-425d-b8e8-6d65c74dc972"
)