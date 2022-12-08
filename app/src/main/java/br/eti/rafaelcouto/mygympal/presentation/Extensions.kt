package br.eti.rafaelcouto.mygympal.presentation

fun String.toIntOrZero(): Int {
    return try {
        this.toInt()
    } catch (e: Exception) {
        0
    }
}
