package net.mythicisland.spark.minestom

import me.lucko.spark.common.platform.world.WorldInfoProvider
import net.minestom.server.MinecraftServer
import net.minestom.server.instance.Instance

internal object MinestomWorldInfoProvider : WorldInfoProvider {

    override fun pollCounts(): WorldInfoProvider.CountsResult {
        val instances = MinecraftServer.getInstanceManager().instances
        return WorldInfoProvider.CountsResult(
            MinecraftServer.getConnectionManager().onlinePlayers.size,
            instances.sumOf { it.entities.size },
            0,
            instances.sumOf { it.chunks.size },
        )
    }

    override fun pollChunks(): WorldInfoProvider.ChunksResult<MinestomChunkInfo> {
        val result = WorldInfoProvider.ChunksResult<MinestomChunkInfo>()
        for (instance in MinecraftServer.getInstanceManager().instances) {
            val chunks = instance.chunks.map { MinestomChunkInfo(it, instance.getChunkEntities(it)) }
            result.put(worldName(instance), chunks)
        }
        return result
    }

    override fun pollGameRules(): WorldInfoProvider.GameRulesResult? = null

    override fun pollDataPacks(): Collection<WorldInfoProvider.DataPackInfo>? = null

    private fun worldName(instance: Instance): String = "${instance.dimensionName} (${instance.uuid})"
}
