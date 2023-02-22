package yasu.MusicCommands;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.managers.AudioManager;
import yasu.lavaplayer.PlayerManager;
import yasu.lavaplayer.TrackScheduler;
import java.util.List;
import java.util.Objects;


public class CmdPlay extends ListenerAdapter {

    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) { //Listens for all "/" events
        if (!event.getName().equals("play")) { //Checking if event = "play"
            return; //If not, we ignore
        }
            Guild guild = event.getGuild();
            if (!event.getMember().getVoiceState().inAudioChannel()) { //Checks if use its in a voice channel
                event.reply("You need to be in a voice channel to use this command").queue();
                return;
            }
            if (!guild.getSelfMember().getVoiceState().inAudioChannel()) { //We check if bot is already in a voice channel, if not we connect it to the VC
                final AudioManager audioManager = event.getGuild().getAudioManager();
                final VoiceChannel memberChannel = (VoiceChannel) event.getMember().getVoiceState().getChannel();
                audioManager.openAudioConnection(memberChannel);
            }
            List<OptionMapping> option1 = event.getOptions(); //We store the Link passed by the user
            String link = option1.get(0).getAsString();

            TextChannel textChannel = event.getChannel().asTextChannel();
            PlayerManager.getINSTANCE().loadAndPlay(textChannel, link,event); //We load the link provided to the loadAndPlay method


    }

    //Auto disconnects bot if it's alone
    @Override
    public void onGuildVoiceUpdate(GuildVoiceUpdateEvent event) { //This event is triggered each time there's an event in any voice channel
        TrackScheduler trackScheduler = PlayerManager.getINSTANCE().getMusicManager(event.getGuild()).scheduler;
        //Objects.requireNonNull -> is used to ensure that the VoiceState is not null
        if (Objects.requireNonNull(event.getGuild().getSelfMember().getVoiceState()).inAudioChannel()) { //Checks if the bot is in a voice channel within the guild
            if (event.getGuild().getSelfMember().getVoiceState().getChannel().getMembers().size() == 1) { //Checks if the bot is the only member in the voice channel. Returns number of members in the channel and equals to 1
                trackScheduler.clearQueue();
                event.getGuild().getAudioManager().closeAudioConnection(); //Close the audio connection, so makes the bot disconnects from the channel
            }
        }

    }


}
