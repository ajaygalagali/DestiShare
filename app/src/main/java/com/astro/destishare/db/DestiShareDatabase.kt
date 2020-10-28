package com.astro.destishare.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.astro.destishare.models.NotificationModel


@Database(
    entities = [NotificationModel::class],
    version = 1
)
@TypeConverters(DestiShareConverters::class)
abstract class DestiShareDatabase() : RoomDatabase(){
    abstract fun getDestiShareDOA() : DestiShareDAO

    companion object{

        private var instance:DestiShareDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance?: synchronized(LOCK){
            instance ?: createDatabase(context).also { instance = it }
        }

        private fun createDatabase(context: Context) =
            Room.databaseBuilder(context.applicationContext,
                DestiShareDatabase::class.java,
                "desti_share_db").build()
    }
}