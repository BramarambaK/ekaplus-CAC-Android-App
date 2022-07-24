package com.eka.cacapp.data.diseaseIden

data class DiseaseIdenListData (
    val _id: Int,
    val title: String,
    val subtitle: String,
    val description: String,
    var imageName: String,
    val imageUrl: String,
    val name: String,
    val uploadedDate: String,
    val uploadedTime: String
)
