package com.example.proyectofingrado.Interfaces

import java.util.*

interface CurrentTime {
    fun calculateCurrentTime():String{
        var currentTime = GregorianCalendar.getInstance().get(Calendar.HOUR_OF_DAY)
        if(currentTime < 12){//Mañana
            return "mañana"
        }else if(currentTime in 13..21){//Tarde
            return "mediodia"
        }else{//Noche
            return "noche"
        }
    }
}