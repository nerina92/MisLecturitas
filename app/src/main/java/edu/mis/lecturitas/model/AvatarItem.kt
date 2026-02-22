package edu.mis.lecturitas.model

import androidx.annotation.DrawableRes

/**
 * Modelo para representar un item de avatar que se puede desbloquear
 */
data class AvatarItem(
    val id: String = "",
    val name: String = "",
    val category: AvatarCategory = AvatarCategory.HAT,
    @DrawableRes val resourceId: Int = 0, // ID del drawable resource
    val emoji: String = "", // Emoji alternativo si no hay drawable
    val pointsCost: Int = 0,
    val unlocked: Boolean = false
) {
    companion object {
        /**
         * Obtiene todos los items de avatar disponibles en el juego
         * Nota: Los resourceId se deben actualizar cuando se agreguen los drawables reales
         */
        fun getAllItems(): List<AvatarItem> {
            return getCharacters() + getHats() + getAccessories() + getBackgrounds() + getFrames()
        }

        fun getCharacters(): List<AvatarItem> {
            return listOf(
                AvatarItem("character_1", "Niño", AvatarCategory.CHARACTER, 0, "👦", 0, true),
                AvatarItem("character_2", "Niña", AvatarCategory.CHARACTER, 0, "👧", 0, true),
                AvatarItem("character_3", "Robot", AvatarCategory.CHARACTER, 0, "🤖", 0, true),
                AvatarItem("character_4", "Gato", AvatarCategory.CHARACTER, 0, "🐱", 0, true),
                AvatarItem("character_5", "Perro", AvatarCategory.CHARACTER, 0, "🐶", 0, true),
                AvatarItem("character_6", "Conejo", AvatarCategory.CHARACTER, 0, "🐰", 0, true)
            )
        }

        fun getHats(): List<AvatarItem> {
            return listOf(
                AvatarItem("hat_none", "Sin sombrero", AvatarCategory.HAT, 0, "", 0, true),
                AvatarItem("hat_cap", "Gorra", AvatarCategory.HAT, 0, "🧢", 10),
                AvatarItem("hat_wizard", "Mago", AvatarCategory.HAT, 0, "🧙", 15),
                AvatarItem("hat_crown", "Corona", AvatarCategory.HAT, 0, "👑", 20),
                AvatarItem("hat_astronaut", "Astronauta", AvatarCategory.HAT, 0, "👨‍🚀", 25),
                AvatarItem("hat_pirate", "Pirata", AvatarCategory.HAT, 0, "🏴‍☠️", 20),
                AvatarItem("hat_chef", "Chef", AvatarCategory.HAT, 0, "👨‍🍳", 15),
                AvatarItem("hat_cowboy", "Vaquero", AvatarCategory.HAT, 0, "🤠", 15),
                AvatarItem("hat_detective", "Detective", AvatarCategory.HAT, 0, "🕵️", 20),
                AvatarItem("hat_graduation", "Graduación", AvatarCategory.HAT, 0, "🎓", 30)
            )
        }

        fun getAccessories(): List<AvatarItem> {
            return listOf(
                AvatarItem("acc_none", "Sin accesorio", AvatarCategory.ACCESSORY, 0, "", 0, true),
                AvatarItem("acc_glasses", "Lentes", AvatarCategory.ACCESSORY, 0, "👓", 10),
                AvatarItem("acc_sunglasses", "Lentes de sol", AvatarCategory.ACCESSORY, 0, "🕶️", 15),
                AvatarItem("acc_bow", "Moño", AvatarCategory.ACCESSORY, 0, "🎀", 10),
                AvatarItem("acc_flower", "Flor", AvatarCategory.ACCESSORY, 0, "🌸", 10),
                AvatarItem("acc_headphones", "Audífonos", AvatarCategory.ACCESSORY, 0, "🎧", 20),
                AvatarItem("acc_mask", "Antifaz", AvatarCategory.ACCESSORY, 0, "🎭", 15),
                AvatarItem("acc_bandana", "Pañuelo", AvatarCategory.ACCESSORY, 0, "🧣", 10)
            )
        }

        fun getBackgrounds(): List<AvatarItem> {
            return listOf(
                AvatarItem("bg_default", "Por defecto", AvatarCategory.BACKGROUND, 0, "⬜", 0, true),
                AvatarItem("bg_space", "Espacio", AvatarCategory.BACKGROUND, 0, "🌌", 25),
                AvatarItem("bg_ocean", "Océano", AvatarCategory.BACKGROUND, 0, "🌊", 25),
                AvatarItem("bg_forest", "Bosque", AvatarCategory.BACKGROUND, 0, "🌳", 25),
                AvatarItem("bg_rainbow", "Arcoíris", AvatarCategory.BACKGROUND, 0, "🌈", 30),
                AvatarItem("bg_stars", "Estrellas", AvatarCategory.BACKGROUND, 0, "✨", 30)
            )
        }

        fun getFrames(): List<AvatarItem> {
            return listOf(
                AvatarItem("frame_default", "Por defecto", AvatarCategory.FRAME, 0, "⬜", 0, true),
                AvatarItem("frame_gold", "Dorado", AvatarCategory.FRAME, 0, "🟡", 40),
                AvatarItem("frame_rainbow", "Arcoíris", AvatarCategory.FRAME, 0, "🌈", 35),
                AvatarItem("frame_star", "Estrella", AvatarCategory.FRAME, 0, "⭐", 30),
                AvatarItem("frame_heart", "Corazón", AvatarCategory.FRAME, 0, "💗", 30)
            )
        }
    }
}

enum class AvatarCategory {
    CHARACTER,
    HAT,
    ACCESSORY,
    BACKGROUND,
    FRAME
}
