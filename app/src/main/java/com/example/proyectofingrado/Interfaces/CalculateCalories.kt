package com.example.proyectofingrado.Interfaces

interface CalculateCalories {

    fun makeCalculate(peso:String, altura:String, sexo:String, actividad:String, edad:String,):Int{
        //Convierto las variables en números en caso de ser necesario para hacer el cáculo matemático de las calorias necesarias para consumir
        var peso = peso.toInt()
        var altura = altura.toInt()
        var edad = edad.toInt()
        //En el caso de ser hombre
        if (sexo=="Hombre"){
            when (actividad) {
                "Sedentaria" -> return ((66 + (13.7 * peso)+((5 * altura)-(6.8 * edad))*1)/4).toInt()
                "Moderada" -> return ((66 + (13.7 * peso)+((5 * altura)-(6.8 * edad))*1.5)/4).toInt()
                "Intensa" -> return ((66 + (13.7 * peso)+((5 * altura)-(6.8 * edad))*1.8)/4).toInt()
                else -> { return 1 }
            }
        }else{
         //En el caso de ser mujer
            when (actividad) {
                "Sedentaria" -> return ((655 + (9.6 * peso)+((1.8 * altura)-(4.7 * edad))*1)/4).toInt()
                "Moderada" -> return ((655 + (9.6 * peso)+((1.8 * altura)-(4.7 * edad))*1.5)/4).toInt()
                "Intensa" -> return ((655 + (9.6 * peso)+((1.8 * altura)-(4.7 * edad))*1.8)/4).toInt()
                else -> { return 1 }
            }
        }
    }
}