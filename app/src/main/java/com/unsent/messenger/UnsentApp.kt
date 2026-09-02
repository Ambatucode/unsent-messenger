package com.unsent.messenger

import android.app.Application
import android.content.Context
import com.unsent.messenger.data.AppDatabase
import com.unsent.messenger.data.MessageRepository

class UnsentApp : Application() {

    lateinit var database: AppDatabase
        private set

    lateinit var repository: MessageRepository
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        database = AppDatabase.getDatabase(this)
        repository = MessageRepository(database.messageDao(), database.conversationDao())
    }

    companion object {
        private var instance: UnsentApp? = null

        fun getRepository(context: Context): MessageRepository {
            val app = instance
            if (app != null) {
                return app.repository
            }
            val db = AppDatabase.getDatabase(context.applicationContext)
            return MessageRepository(db.messageDao(), db.conversationDao())
        }

        fun getInstance(): UnsentApp? = instance
    }
}
