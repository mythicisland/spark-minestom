package net.mythicisland.spark.minestom

import me.lucko.spark.common.tick.AbstractTickHook
import net.minestom.server.MinecraftServer
import net.minestom.server.event.EventNode
import net.minestom.server.event.server.ServerTickMonitorEvent

internal class MinestomTickHook : AbstractTickHook() {

    private val node = EventNode.all("spark-tick-hook")
        .addListener(ServerTickMonitorEvent::class.java) { onTick() }

    override fun start() {
        MinecraftServer.getGlobalEventHandler().addChild(node)
    }

    override fun close() {
        MinecraftServer.getGlobalEventHandler().removeChild(node)
    }
}
