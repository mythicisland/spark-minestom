package net.mythicisland.spark.minestom

import net.minestom.server.command.CommandSender
import net.minestom.server.entity.Player
import java.nio.file.Path
import java.util.function.BiPredicate

internal class SparkMinestomBuilder(
    private val directory: Path,
    private val commands: Boolean = true,
    private val permissionHandler: BiPredicate<CommandSender, String> = BiPredicate { sender, _ ->
        sender is Player && sender.permissionLevel >= OPERATOR_PERMISSION_LEVEL
    },
) : SparkMinestom.Builder {

    override fun commands(commands: Boolean): SparkMinestom.Builder =
        SparkMinestomBuilder(directory, commands, permissionHandler)

    override fun permissionHandler(permissionHandler: BiPredicate<CommandSender, String>): SparkMinestom.Builder =
        SparkMinestomBuilder(directory, commands, permissionHandler)

    override fun enable(): SparkMinestom {
        val plugin = MinestomSparkPlugin(directory, commands, permissionHandler)
        plugin.enable()
        return plugin
    }

    companion object {
        private const val OPERATOR_PERMISSION_LEVEL = 4
    }
}
