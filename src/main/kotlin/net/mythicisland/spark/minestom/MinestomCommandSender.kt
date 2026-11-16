package net.mythicisland.spark.minestom

import me.lucko.spark.common.command.sender.AbstractCommandSender
import net.kyori.adventure.text.Component
import net.minestom.server.command.CommandSender
import net.minestom.server.command.ConsoleSender
import net.minestom.server.entity.Player
import java.util.UUID
import java.util.function.BiPredicate

internal class MinestomCommandSender(
    sender: CommandSender,
    private val permissionHandler: BiPredicate<CommandSender, String>,
) : AbstractCommandSender<CommandSender>(sender) {

    override fun getName(): String = when (val sender = delegate) {
        is Player -> sender.username
        is ConsoleSender -> "Console"
        else -> "unknown:${sender.javaClass.simpleName}"
    }

    override fun getUniqueId(): UUID? = (delegate as? Player)?.uuid

    override fun sendMessage(message: Component) {
        delegate.sendMessage(message)
    }

    override fun hasPermission(permission: String): Boolean =
        delegate is ConsoleSender || permissionHandler.test(delegate, permission)
}
