package com.example.tft.model.detail_stage


import android.health.connect.datatypes.SleepSessionRecord
import com.example.tft.model.pilot.Country
import com.example.tft.model.pilot.Sport
import com.example.tft.model.pilot.TeamColors


data class SubstagesResponse(
    val stages: List<Stage>
)

data class Stage(
    val uniqueStage: UniqueStage,
    val description: String,
    val slug: String,
    val type: Type,
    val status: Status,
    val year: String,
    val id: Int,
    val country: Country,
    val name: String,
    val info: Info,
    val startDateTimestamp: Long,
    val endDateTimestamp: Long,
    val seasonStageName: String,
    val winner: Winner,
    val flag: String,
    val stageParent: StageParent
)


data class Type(
    val id: Int,
    val name: String
)

data class Status(
    val description: String,
    val type: String
)

data class Info(
    val circuit: String,
    val circuitCity: String,
    val circuitCountry: String
)

data class Winner(
    val name: String,
    val slug: String,
    val shortName: String,
    val sport: Sport,
    val userCount: Int,
    val nameCode: String,
    val national: Boolean,
    val type: Int,
    val id: Int,
    val country: Country,
    val teamColors: TeamColors,
    val fieldTranslations: FieldTranslations?
)

data class FieldTranslations(
    val nameTranslation: Map<String, String>?,
    val shortNameTranslation: Map<String, String>?
)

data class StageParent(
    val description: String,
    val id: Int,
    val startDateTimestamp: Long
)
data class SeasonsResponse(
    val seasons: List<Season>
)

data class Season(
    val uniqueStage: UniqueStage,
    val description: String,
    val slug: String,
    val year: String,
    val id: Int,
    val name: String,
    val startDateTimestamp: Long,
    val endDateTimestamp: Long,
    val imageUrl: String // Nuevo campo para la URL de la imagen
)

data class UniqueStage(
    val category: Category,
    val name: String,
    val slug: String,
    val id: Int
)

data class Category(
    val name: String,
    val slug: String,
    val sport: Sport,
    val id: Int,
    val flag: String
)


//Imagen de cada temporada
data class SeasonWithImage(
    val season: Season,
    val imageUrl: String
)

data class DayDetail(
    val day: Int,
    val winner: Winner,
    val description: String
)

data class DaysDetailResponse(
    val days: List<DayDetail>
)

data class ImageResponse(
    val imageUrl: String
)
data class RaceResponse(
    val races: List<Race>
)

data class Race(
    val stage: Stage,
    val position: Int,
    val points: Int?,
    val updatedAtTimestamp: Long
)
