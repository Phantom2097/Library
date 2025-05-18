package ru.phantom.common.entities.library

/**
 * Интерфейс добавляет функционал отображения краткой и полной информации о предмете
 */
interface Showable {
    fun briefInformation(): String
    fun fullInformation(): String
}