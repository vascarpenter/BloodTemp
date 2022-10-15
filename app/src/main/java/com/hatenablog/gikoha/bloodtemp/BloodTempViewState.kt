package com.hatenablog.gikoha.bloodtemp

data class BloodTempViewState
    (
    val items: List<BloodTemp>?
)
{
    companion object
    {
        val EMPTY = BloodTempViewState(null)
    }
}