package com.unsent.messenger

import android.app.Application
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
        lateinit var instance: UnsentApp
            private set
    }
}
