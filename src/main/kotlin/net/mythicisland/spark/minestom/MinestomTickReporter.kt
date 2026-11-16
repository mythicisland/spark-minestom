package net.mythicisland.spark.minestom

import me.lucko.spark.common.tick.AbstractTickReporter
import net.minestom.server.MinecraftServer
import net.minestom.server.event.EventNode
import net.minestom.server.event.server.ServerTickMonitorEvent

internal class MinestomTickReporter : AbstractTickReporter() {

    private val node = EventNode.all("spark-tick-reporter")
        .addListener(ServerTickMonitorEvent::class.java) { event -> onTick(event.tickMonitor.tickTime) }

    override fun start() {
        MinecraftServer.getGlobalEventHandler().addChild(node)
    }

    override fun close() {
        MinecraftServer.getGlobalEventHandler().removeChild(node)
    }
}
