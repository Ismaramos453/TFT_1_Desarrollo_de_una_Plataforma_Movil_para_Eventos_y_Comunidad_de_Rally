package com.example.tft.data.api.youtube

data class YouTubeVideoResponse(
    val items: List<YouTubeVideo>
)

data class YouTubeVideo(
    val id: String,
    val snippet: Snippet
)

data class Snippet(
    val title: String,
    val description: String,
    val thumbnails: Thumbnails,
    val channelTitle: String
)

data class Thumbnails(
    val default: Thumbnail
)

data class Thumbnail(
    val url: String
)

data class YouTubeSearchResponse(
    val items: List<YouTubeSearchResult>
)

data class YouTubeSearchResult(
    val id: VideoId,
    val snippet: Snippet
)

data class VideoId(
    val videoId: String
)