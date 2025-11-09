package dev.rukhlovar

import dev.kord.core.Kord
import dev.kord.core.event.interaction.ChatInputCommandInteractionCreateEvent
import dev.rukhlovar.commands.Command

class CommandRegistry(private val kord: Kord) {
    private val _commands = mutableMapOf<String, Command>()

    fun register(command: Command) {
        _commands[command.name] = command
    }

    fun register(commands: List<Command>) {
        commands.forEach { command ->
            register(command)
        }
    }

    suspend fun registerAll() {
        _commands.values.forEach { command ->
            kord.createGlobalChatInputCommand(command.name, command.description) {
                command.configure(this)
            }
        }
    }

    suspend fun handleEvent(event: ChatInputCommandInteractionCreateEvent) {
        val commandName = event.interaction.command.rootName
        _commands[commandName]?.execute(event)
    }
}
