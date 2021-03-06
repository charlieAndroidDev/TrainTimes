package com.cniekirk.traintimes.model.servicedetails.req

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "soap:Body")
data class ServiceDetailsBody(
    @Element val getServiceDetailsByRIDRequest: GetServiceDetailsByRIDRequest
)