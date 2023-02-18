package yasu;

import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

import java.util.ArrayList;
import java.util.List;

public class CommandManager extends ListenerAdapter {
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event){
        if(event.getName().equals("play")){

        }
    }
    public void onGuildReady(GuildReadyEvent event){
        //Variable type List to store all commands
        List<CommandData> commandList = new ArrayList<>();
        //Adding commands to the list
        commandList.add(Commands.slash("play","Play a song"));
        //Registering commandList to JDA API (bot)
        event.getGuild().updateCommands().addCommands(commandList).queue();
    }
}
