package net.mythicisland.spark.minestom

import net.kyori.adventure.identity.Identity
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import net.minestom.server.command.CommandSender
import net.minestom.server.tag.TagHandler
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit

class TestCommandSender : CommandSender {

    private val messages = LinkedBlockingQueue<String>()
    private val tagHandler = TagHandler.newHandler()

    override fun sendMessage(message: Component) {
        messages.add(PlainTextComponentSerializer.plainText().serialize(message))
    }

    override fun tagHandler(): TagHandler = tagHandler

    override fun identity(): Identity = Identity.nil()

    fun awaitMessage(text: String): Boolean {
        val deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(TIMEOUT_SECONDS)
        while (System.nanoTime() < deadline) {
            val message = messages.poll(POLL_MILLIS, TimeUnit.MILLISECONDS) ?: continue
            if (text in message) {
                return true
            }
        }
        return false
    }

    companion object {
        private const val TIMEOUT_SECONDS = 60L
        private const val POLL_MILLIS = 100L
    }
}
