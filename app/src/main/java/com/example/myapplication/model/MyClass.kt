package com.example.myapplication.model

class MyClass {
    private val _data: Any? = null // Variable privada para almacenar el valor

    // Propiedad pública para acceder a _data
    val data: Any?
        get() = _data
}