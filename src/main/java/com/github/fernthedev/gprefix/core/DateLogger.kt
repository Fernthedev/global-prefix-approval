package com.github.fernthedev.gprefix.core

import com.github.fernthedev.fernapi.universal.api.FernCommandIssuer
import com.github.fernthedev.fernapi.universal.api.IFPlayer
import com.github.fernthedev.gprefix.core.db.PrefixInfoData
import okio.buffer
import okio.sink
import okio.source
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class DateLogger(private val folder: File?) {
    private lateinit var file: File


    fun writeLog(staff: FernCommandIssuer, otherPlayer: IFPlayer<*>, prefixInfoData: PrefixInfoData) {
        if (Core.getPrefixPlugin().coreConfig.configData.isWriteLogs) {
            validateFile()
            file.sink().use { fileSink ->
                fileSink.buffer().use { bufferedSink ->
                    val stringList = readLines()!!

                    var staffMsg = if (staff is IFPlayer<*>) "Staff: ${staff.uniqueId} (${staff.name})"
                    else "Console"

                    stringList.add("[${timeFormat.format(Date())} ] $staffMsg managed request: ${otherPlayer.name} [${otherPlayer.uniqueId}] = (${prefixInfoData.prefixUpdateMode}) ${prefixInfoData.prefix}")
                    for (string in stringList) {
                        bufferedSink.writeUtf8(string)
                    }
                }
            }
        }
    }

    fun writeLog(otherPlayer: IFPlayer<*>, prefixInfoData: PrefixInfoData) {
        if (Core.getPrefixPlugin().coreConfig.configData.isWriteLogs) {
            validateFile()
            file.sink().use { fileSink ->
                fileSink.buffer().use { bufferedSink ->
                    val stringList = readLines()!!
                    stringList.add("[${timeFormat.format(Date())}] ${otherPlayer.uniqueId} (${otherPlayer.name}) requested: = (${prefixInfoData.prefixUpdateMode}) ${prefixInfoData.prefix}")
                    for (string in stringList) {
                        bufferedSink.writeUtf8(string)
                    }
                }
            }
        }
    }

    @Throws(IOException::class)
    fun readLines(): MutableList<String>? {
        if (Core.getPrefixPlugin().coreConfig.configData.isWriteLogs) {
            validateFile()
            file.source().use { fileSource ->
                fileSource.buffer().use { bufferedSource ->
                    val stringsList: MutableList<String> = ArrayList()
                    while (true) {
                        val line = bufferedSource.readUtf8Line() ?: break
                        stringsList.add(line)
                    }
                    return stringsList
                }
            }
        }
        return null;
    }

    companion object {
        private val timeFormat = SimpleDateFormat("yyyy-MM-dd-hh-mm-ss")
    }

    fun validateFile() {
        if (Core.getPrefixPlugin().coreConfig.configData.isWriteLogs) {
            file = File(folder, "${timeFormat.format(Date())}.log")
            if (!file.exists()) {
                try {
                    file.createNewFile()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }
}