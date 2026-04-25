package dev.rukhlovar

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.event.interaction.ChatInputCommandInteractionCreateEvent
import dev.rukhlovar.commands.Command
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class CommandRegistry(private val kord: Kord) {
    private val _commands = mutableMapOf<String, Command>()
    private val scope = CoroutineScope(kord.coroutineContext)
    private val localGuildId = Snowflake(435098282130276352) // TODO remove

    private fun register(command: Command) {
        scope.launch {
            deleteCommands(localGuildId)
            _commands[command.name] = command
            kord.createGuildChatInputCommand( // TODO replace on global
                guildId = localGuildId,
                name = command.name,
                description = command.description,
            ) {
                command.configure(this)
            }
            println("Registered command ${command.name}")
        }
    }

    fun register(commands: List<Command>) {
        commands.forEach { command ->
            register(command)
        }
    }

    private suspend fun deleteCommands(guildId: Snowflake) {
        kord.getGuildApplicationCommands(guildId).collect { command ->
            command.delete()
        }
    }

    suspend fun handleEvent(event: ChatInputCommandInteractionCreateEvent) {
        val commandName = event.interaction.command.rootName
        _commands[commandName]?.execute(event)
        println("Command $commandName executed")
    }
}
