package com.molloyruaidhri.realmsync

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import org.bson.types.ObjectId
import java.util.*

//After
open class Note(
    var noteName: String= "",
    var date: Date = Date(),
    var _partition: String = "Public"
): RealmObject(){

    @PrimaryKey
    var _id: ObjectId = ObjectId()

}