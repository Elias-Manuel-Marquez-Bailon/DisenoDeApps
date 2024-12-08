package com.example.myapplication.model

data class LightReading (
    val timestamp: Long,
    //val lux: Float
    val lightLevel : Float,
    //Nos falto almacenar modo
    val mode : String
) {
    override fun toString(): String {
        val formatearMarca = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
            java.util.Locale.getDefault()).format(java.util.Date(timestamp))
        return "Lectura de luz: \n"+
                "Fecha y Hora: $formatearMarca\n" +
                "Nivel de Luz: $lightLevel lux"
    }
}