package edu.mis.lecturitas.model

/**
 * Modelo para la personalización del avatar del usuario
 */
data class AvatarCustomization(
    val baseCharacter: String = "character_1",
    val hat: String? = null,
    val accessory: String? = null,
    val background: String = "bg_default",
    val frame: String = "frame_default"
) {
    companion object {
        // IDs de personajes base disponibles
        val BASE_CHARACTERS = listOf(
            "character_1", // Niño
            "character_2", // Niña
            "character_3", // Robot
            "character_4", // Gato
            "character_5", // Perro
            "character_6"  // Conejo
        )

        // IDs de sombreros disponibles
        val HATS = listOf(
            "hat_none",     // Sin sombrero (0 puntos)
            "hat_cap",      // Gorra (10 puntos)
            "hat_wizard",   // Mago (15 puntos)
            "hat_crown",    // Corona (20 puntos)
            "hat_astronaut",// Astronauta (25 puntos)
            "hat_pirate",   // Pirata (20 puntos)
            "hat_chef",     // Chef (15 puntos)
            "hat_cowboy",   // Vaquero (15 puntos)
            "hat_detective",// Detective (20 puntos)
            "hat_graduation"// Graduación (30 puntos)
        )

        // IDs de accesorios disponibles
        val ACCESSORIES = listOf(
            "acc_none",       // Sin accesorio (0 puntos)
            "acc_glasses",    // Lentes (10 puntos)
            "acc_sunglasses", // Lentes de sol (15 puntos)
            "acc_bow",        // Moño (10 puntos)
            "acc_flower",     // Flor (10 puntos)
            "acc_headphones", // Audífonos (20 puntos)
            "acc_mask",       // Antifaz (15 puntos)
            "acc_bandana"     // Pañuelo (10 puntos)
        )

        // IDs de fondos disponibles
        val BACKGROUNDS = listOf(
            "bg_default",   // Por defecto (0 puntos)
            "bg_space",     // Espacio (25 puntos)
            "bg_ocean",     // Océano (25 puntos)
            "bg_forest",    // Bosque (25 puntos)
            "bg_rainbow",   // Arcoíris (30 puntos)
            "bg_stars"      // Estrellas (30 puntos)
        )

        // IDs de marcos disponibles
        val FRAMES = listOf(
            "frame_default",  // Por defecto (0 puntos)
            "frame_gold",     // Dorado (40 puntos)
            "frame_rainbow",  // Arcoíris (35 puntos)
            "frame_star",     // Estrella (30 puntos)
            "frame_heart"     // Corazón (30 puntos)
        )
    }
}
