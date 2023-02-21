package yasu;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.w3c.dom.Text;
import yasu.lavaplayer.GuildMusicManager;
import yasu.lavaplayer.PlayerManager;
import yasu.lavaplayer.TrackScheduler;

import java.util.ArrayList;
import java.util.List;

import static com.sedmelluq.discord.lavaplayer.container.matroska.format.MatroskaElementType.Audio;
import static com.sedmelluq.discord.lavaplayer.container.matroska.format.MatroskaElementType.Tracks;

public class CommandManager extends ListenerAdapter {
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        //To work with anything from audioplayer we have to call the Instance, then the GuildMusicManager to get the Object of that guild associated with that text channel where command had been sent.
        List<AudioTrack> tracks = PlayerManager.getINSTANCE().getMusicManager(event.getGuild()).scheduler.getQueue(); //Variable list type tracks that stores all tracks in the queue. (We call getQueue() method to get the queue. Class from TrackScheduler)
        TrackScheduler trackScheduler = PlayerManager.getINSTANCE().getMusicManager(event.getGuild()).scheduler; //We initialize trackScheduler object to be able to work with the methods of this class.
        PlayerManager playerManager = PlayerManager.getINSTANCE();

        if (event.getFullCommandName().contains("skip")) {
            event.deferReply().queue();
            trackScheduler.nextTrack();
            AudioTrack track = tracks.get(1); //Getting info of track 1 from list
            AudioTrackInfo info = track.getInfo(); //Getting info
            StringBuilder sb = new StringBuilder(); //Building the message
                sb.append("**Next track:** ");
                sb.append("```");
                sb.append(info.title);
                sb.append(" by ");
                sb.append(info.author);
                sb.append("```");
            event.getHook().sendMessage(sb.toString()).queue();
        } else if (event.getFullCommandName().contains("pause")) {
            event.deferReply().queue();
            trackScheduler.pauseTrack();
            event.getHook().sendMessage("Track had been paused").queue();
        }else if(event.getName().equals("resume")){
            event.deferReply().queue();
            trackScheduler.resumeTrack();
            event.getHook().sendMessage("Track had been resumed!").queue();
        }else if (event.getFullCommandName().contains("volume")) {
            event.deferReply().queue();
            List<OptionMapping> option2 = event.getOptions();
            int volume = option2.get(0).getAsInt(); //Option typed by the user. Have to be an int
            int currentvolume = trackScheduler.getVolume();
            trackScheduler.setVolume(volume);
            event.getHook().sendMessage("```Current volume: " + currentvolume + "```" + "```Modified Volume: " + volume + "```").queue();
            trackScheduler.setVolume(volume);
        } else if (event.getFullCommandName().contains("queue")) {
            event.deferReply().queue();
            StringBuilder sb = new StringBuilder(); //Calling StringBuilder object to make the output readable.
            sb.append("**Queue:**\n"); //Method append --> to add Queue followed by strings
            for (int i = 0; i < tracks.size(); i++) { //Starting a loop that will iterate over the list of audio tracks
                AudioTrack track = tracks.get(i); //Declares a variable track of type AudioTrack and sets it to the AudioTrack object at index i of the tracks list.
                AudioTrackInfo info = track.getInfo(); //variable info of type AudioTrackInfo and sets it to the AudioTrackInfo object for the current AudioTrack.
                sb.append(String.format("```" + "%d. %s by %s\n", i + 1, info.title, info.author + "```")); //Making the output.
            }
            event.getHook().sendMessage(sb.toString()).queue();
        } else if (event.getFullCommandName().contains("clear")) {
            event.deferReply();
            trackScheduler.clearQueue();
            event.getHook().sendMessage("Queue had been succesfully clear!").queue();
        }else if(event.getFullCommandName().contains("disconnect")){
            trackScheduler.clearQueue();
            event.getGuild().getAudioManager().closeAudioConnection();
            event.reply("Bot had disconnected from VC").queue();
        }else if(event.getFullCommandName().contains("resume")){
            trackScheduler.resumeTrack();
            event.reply("Track had been resumed").queue();
        }else if(event.getFullCommandName().contains("playingnow")){
            trackScheduler.playingNow();
            event.reply("**Track being played: **" + trackScheduler.playingNow());
        }
    }

    public void onGuildReady(GuildReadyEvent event) {
        //Variable type List to store all commands
        List<CommandData> commandList = new ArrayList<>();
        //Adding commands to the list
        OptionData option1 = new OptionData(OptionType.STRING, "song", "link your song", true);
        commandList.add(Commands.slash("play", "Play a song").addOptions(option1));
        commandList.add(Commands.slash("skip", "Skip the current song"));
        commandList.add(Commands.slash("pause", "Pause the current track"));
        OptionData option2 = new OptionData(OptionType.INTEGER, "volume", "Set the volume of the bot. 0-100", true);
        commandList.add(Commands.slash("volume", "set a volume!").addOptions(option2));
        commandList.add(Commands.slash("queue", "List all tracks in queue"));
        commandList.add(Commands.slash("clear", "Clear all tracks in the queue"));
        commandList.add(Commands.slash("disconnect","disconnect bot from the VC"));
        commandList.add(Commands.slash("resume","Resume the track"));
        //Registering commandList to JDA API (bot)
        event.getGuild().updateCommands().addCommands(commandList).queue();


    }
}
