package yasu;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import yasu.lavaplayer.PlayerManager;
import yasu.lavaplayer.TrackScheduler;

import java.util.ArrayList;
import java.util.List;

import static com.sedmelluq.discord.lavaplayer.container.matroska.format.MatroskaElementType.Tracks;

public class CommandManager extends ListenerAdapter {
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event){

        TrackScheduler trackScheduler = PlayerManager.getINSTANCE().getMusicManager(event.getGuild()).scheduler;
        if(event.getName().equals("next")){
            //We are initializing a trackscheduler object with the audioplayer of that Guild
            trackScheduler.nextTrack();
            event.reply("Next Track Coming: " ); //Pending to add author and title
        }else if(event.getName().equals("pause")){
            event.reply("Track had been paused!");
            trackScheduler.pauseTrack();
        }else if(event.getName().equals("volume")){
            List<OptionMapping> option2= event.getOptions();
            int volume = option2.get(0).getAsInt();
            event.reply("Volume had been modified");
            trackScheduler.setVolume(volume);
        }
    }
    public void onGuildReady(GuildReadyEvent event){
        //Variable type List to store all commands
        List<CommandData> commandList = new ArrayList<>();
        //Adding commands to the list
        OptionData option1  = new OptionData(OptionType.STRING, "song","link your song",true);
        commandList.add(Commands.slash("play","Play a song").addOptions(option1));
        commandList.add(Commands.slash("next","Play the next song"));
        commandList.add(Commands.slash("pause","Pause the current track"));
        OptionData option2 = new OptionData(OptionType.INTEGER,"volume","Set the volume of the bot. 0-100", true);
        commandList.add(Commands.slash("volume","set a volume!").addOptions(option2));
        //Registering commandList to JDA API (bot)
        event.getGuild().updateCommands().addCommands(commandList).queue();


    }
}
