package com.rittmann.passwordnotify.data.dao.room.config

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.rittmann.passwordnotify.data.basic.Login
import com.rittmann.passwordnotify.data.dao.room.interfaces.ManagerPasswordDAO
import com.rittmann.passwordnotify.data.basic.ManagerPassword
import com.rittmann.passwordnotify.data.dao.room.interfaces.LoginDAO

@Database(entities = [ManagerPassword::class, Login::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun managerPasswordDao(): ManagerPasswordDAO
    abstract fun loginDao(): LoginDAO

    companion object {
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase? {
            if (INSTANCE == null) {
                synchronized(AppDatabase::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = Room.databaseBuilder(
                            context.applicationContext,
                            AppDatabase::class.java, "managerPasswordDB"
                        ).build()
                    }
                }
            }
            return INSTANCE
        }
    }
}