package net.mythicisland.spark.minestom

import me.lucko.spark.common.SparkPlatform
import net.minestom.server.command.CommandSender
import net.minestom.server.command.builder.Command
import net.minestom.server.command.builder.arguments.ArgumentType
import net.minestom.server.command.builder.suggestion.SuggestionEntry
import java.util.function.BiPredicate

internal object MinestomSparkCommand {

    fun create(platform: SparkPlatform, permissionHandler: BiPredicate<CommandSender, String>): Command {
        val command = Command("spark")
        val arguments = ArgumentType.StringArray("args")

        arguments.setSuggestionCallback { sender, context, suggestion ->
            val args = context.input.split(" ").drop(1).toTypedArray()
            platform.tabCompleteCommand(MinestomCommandSender(sender, permissionHandler), args)
                .forEach { suggestion.addEntry(SuggestionEntry(it)) }
        }

        command.setCondition { sender, _ ->
            platform.hasPermissionForAnyCommand(MinestomCommandSender(sender, permissionHandler))
        }
        command.setDefaultExecutor { sender, _ ->
            platform.executeCommand(MinestomCommandSender(sender, permissionHandler), emptyArray())
        }
        command.addSyntax({ sender, context ->
            platform.executeCommand(MinestomCommandSender(sender, permissionHandler), context.get(arguments))
        }, arguments)

        return command
    }
}
