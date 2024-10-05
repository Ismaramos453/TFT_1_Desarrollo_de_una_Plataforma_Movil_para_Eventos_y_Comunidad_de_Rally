package com.example.tft.data.api



import com.example.tft.model.detail_stage.ImageResponse
import com.example.tft.model.detail_stage.RaceResponse
import com.example.tft.model.pilot.RallyStandingsResponse2
import com.example.tft.model.detail_stage.SeasonsResponse
import com.example.tft.model.detail_stage.SubstagesResponse
import com.example.tft.model.pilot.PilotDetailResponse
import okhttp3.ResponseBody


import retrofit2.http.GET
import retrofit2.http.Path

interface WrcApiService {
    // Obtener todas las etapas
    @GET("unique-stage/36/season")
    suspend fun getSeasons(): SeasonsResponse

    @GET("unique-stage/{seasonId}/image")
    suspend fun getSeasonImage(@Path("seasonId") seasonId: String): ImageResponse

    // Obtener substages de una etapa específica
    @GET("stage/{stageId}/substages")
    suspend fun getSubstages(@Path("stageId") stageId: String): SubstagesResponse

    // Obtener los standings de una etapa específica
    @GET("stage/{stageId}/standings/competitor")
    suspend fun getStandings(@Path("stageId") stageId: String): RallyStandingsResponse2

    @GET("team/{pilotId}")
    suspend fun getPilotDetail(@Path("pilotId") pilotId: Int): PilotDetailResponse

    @GET("team/{id}/stage/season/{seasonId}/races")
    suspend fun getPilotRaces(
        @Path("id") id: Int,
        @Path("seasonId") seasonId: Int
    ): RaceResponse

}

