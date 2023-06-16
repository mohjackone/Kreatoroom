package br.com.setupbuilder.model

class MotherBoard(name: String, id: String, price: Float, releaseYear:String, brand:String, details:String, image:String,
           ramSlots: String, chipset:String,socket: String, formFactor:String):
    Part(name, id, price, releaseYear, brand, details, image){

    var ramSlots: String = ramSlots
        protected set
    var chipset: String = chipset
        protected set
    var socket: String = socket
        protected set
    var formFactor: String = formFactor
        protected set

    init{
        println("Motherboard berhasil ditambahkan")
    }
}