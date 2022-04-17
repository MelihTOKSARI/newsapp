package com.example.newsapp.utils

import android.util.Log
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class Utils {

    companion object {
        fun getLocalDateTime(stringDate: String): LocalDate {
            Log.i("Utils", "stringDate: $stringDate")
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
            return LocalDate.parse(stringDate, formatter);
        }
    }

}