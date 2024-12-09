package com.example.tft.model.pilot

data class RallyStandingsResponse2(
    val standings: List<Standing>
)

data class Standing(
    val team: Team,
    val points: Int,
    val position: Int,
    val gap: String,
    val status: String,
    val updatedAtTimestamp: Long
)

data class Team(
    val name: String,
    val slug: String,
    val shortName: String,
    val sport: Sport,
    val category: Category,
    val tournament: Tournament,
    val userCount: Int,
    val nameCode: String,
    val national: Boolean,
    val type: Int,
    val id: Int,
    val country: Country,
    val flag: String,
    val teamColors: TeamColors,
    val fieldTranslations: FieldTranslations,
    val fullName: String,
    val playerTeamInfo: PlayerTeamInfo
)

data class PlayerTeamInfo(
    val birthplace: String,
    val id: Int,
    val birthDateTimestamp: Long
)

data class Sport(
    val name: String,
    val slug: String,
    val id: Int
)

data class Category(
    val name: String,
    val slug: String,
    val sport: Sport,
    val id: Int,
    val country: Country?,
    val flag: String
)

data class Tournament(
    val name: String,
    val slug: String,
    val category: Category,
    val priority: Int,
    val isLive: Boolean,
    val id: Int
)

data class Country(
    val alpha2: String?,
    val alpha3: String?,
    val name: String?
)

data class TeamColors(
    val primary: String,
    val secondary: String,
    val text: String
)

data class FieldTranslations(
    val nameTranslation: Map<String, String>?,
    val shortNameTranslation: Map<String, String>?
)
data class PilotDetailResponse(
    val team: Team
)

