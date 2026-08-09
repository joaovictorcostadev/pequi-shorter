package com.joaovictorcostadev.pequi_short.service

import com.joaovictorcostadev.pequi_short.dto.geoip.GeoIpDto
import com.maxmind.geoip2.DatabaseReader
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.stereotype.Service
import java.net.InetAddress

@Service
class GeoIpService (
    @Value("classpath:GeoLite2-City.mmdb")
    private val geoIpResource: Resource
) {
    private val databaseReader: DatabaseReader by lazy {
        geoIpResource.inputStream.use {
            inputStream -> DatabaseReader.Builder(inputStream).build()
        }
    }

    fun getLocation(ipAddress: String) : GeoIpDto {
        try {
            val inetAddress: InetAddress = InetAddress.getByName(ipAddress);
            val response = databaseReader.city(inetAddress)

            val countryName: String = response.country().name() ?: "UNKNOW"
            val stateName: String = response.mostSpecificSubdivision().name() ?: "UNKNOW"
            val cityName: String = response.city().name() ?: "UNKNOW"

            return GeoIpDto(countryName = countryName, stateName =  stateName, cityName = cityName)
        }
        catch (e: Exception) {
            return GeoIpDto(countryName = "UNKNOW", stateName =  "UNKNOW", cityName = "UNKNOW")
        }
    }
}