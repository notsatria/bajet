package com.notsatria.bajet.utils

import android.content.Context
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

object Helper {
    fun loadJsonArray(context: Context, resource: Int, jsonName: String): JSONArray? {
        val builder = StringBuilder()
        val `in` = context.resources.openRawResource(resource)
        val reader = BufferedReader(InputStreamReader(`in`))
        var line: String?
        try {
            while (reader.readLine().also { line = it } != null) {
                builder.append(line)
            }
            val json = JSONObject(builder.toString())
            return json.getJSONArray(jsonName)
        } catch (exception: IOException) {
            exception.printStackTrace()
        } catch (exception: JSONException) {
            exception.printStackTrace()
        }
        return null
    }
}