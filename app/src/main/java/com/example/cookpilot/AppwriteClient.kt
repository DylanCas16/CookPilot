package com.example.cookpilot

import APPWRITE_PROJECT_ID
import APPWRITE_PUBLIC_ENDPOINT
import android.content.Context
import io.appwrite.Client
import io.appwrite.services.Databases

object AppwriteClient {

    lateinit var client: Client
        private set
    lateinit var databases: Databases
        private set

    fun init(context: Context) {
        client = Client(context)
            .setEndpoint(APPWRITE_PUBLIC_ENDPOINT)
            .setProject(APPWRITE_PROJECT_ID)

        println("DEBUG Appwrite: endpoint=$APPWRITE_PUBLIC_ENDPOINT, projectId=$APPWRITE_PROJECT_ID")

        databases = Databases(client)
    }
}
