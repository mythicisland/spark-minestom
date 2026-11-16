package net.mythicisland.spark.minestom

import me.lucko.spark.api.Spark
import net.minestom.server.command.CommandSender
import java.nio.file.Path
import java.util.function.BiPredicate

interface SparkMinestom {

    fun getApi(): Spark

    fun disable()

    interface Builder {

        fun commands(commands: Boolean): Builder

        fun permissionHandler(permissionHandler: BiPredicate<CommandSender, String>): Builder

        fun enable(): SparkMinestom
    }

    companion object {

        @JvmStatic
        fun builder(directory: Path): Builder = SparkMinestomBuilder(directory)
    }
}
