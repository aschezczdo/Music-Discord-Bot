package yasu.MusicCommands;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import yasu.api.Api;
import yasu.lavaplayer.GuildMusicManager;
import yasu.lavaplayer.PlayerManager;
import yasu.lavaplayer.TrackScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AudioPlayerCommands extends ListenerAdapter {
    //private static final Logger logger = LoggerFactory.getLogger(AudioPlayerCommands.class);

    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        //logger.info("Received SlashCommandInteractionEvent for AudioPlayerCommands: " + event.getFullCommandName());
        //To work with anything from audioplayer we have to call the Instance, then the GuildMusicManager to get the Object of that guild associated with that text channel where command had been sent.
        List<AudioTrack> tracks = PlayerManager.getINSTANCE().getMusicManager(event.getGuild()).scheduler.getQueue(); //Variable list type tracks that stores all tracks in the queue. (We call getQueue() method to get the queue. Class from TrackScheduler)
        TrackScheduler trackScheduler = PlayerManager.getINSTANCE().getMusicManager(event.getGuild()).scheduler; //We initialize trackScheduler object to be able to work with the methods of this class.
        PlayerManager playerManager = PlayerManager.getINSTANCE();
        if (event.getFullCommandName().contains("playpause")) {
            event.deferReply().queue();
            //logger.info("Processing play/pause command.");
            if (trackScheduler.audioPlayer.isPaused()) {
                trackScheduler.audioPlayer.setPaused(false);
                event.getHook().sendMessage("Track had been resumed").queue();
            } else {
                trackScheduler.audioPlayer.setPaused(true);
                event.getHook().sendMessage("Track had been paused").queue();

            }
        } else if (event.getFullCommandName().contains("volume")) {
            event.deferReply().queue();
            //logger.info("Processing volume command.");
            List<OptionMapping> option2 = event.getOptions();
            int volume = 0;
            if(option2 == null || option2.isEmpty()){
                event.getHook().sendMessage("Volume value missing.").queue();
                return;
            }
            volume = option2.get(0).getAsInt();
            int currentvolume = trackScheduler.getVolume();
            if (volume <= 100 && volume >= 0) {
                event.reply(("Changing the volume...")).queue();
                trackScheduler.setVolume(volume);
                event.getHook().sendMessage("```Current volume: " + currentvolume + "```" + "```Modified Volume: " + volume + "```").queue();
            } else {
                event.getHook().sendMessage("Volume must be between 0-100").queue();
            }
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
            event.reply("Removing tracks").queue();
            trackScheduler.clearQueue();
            event.getHook().sendMessage("Queue had been succesfully clear!").queue();
        } else if (event.getFullCommandName().contains("disconnect")) {
            event.deferReply().queue();
            trackScheduler.clearQueue();
            event.getGuild().getAudioManager().closeAudioConnection();
            event.getHook().sendMessage("Bot had been disconnected from VC").queue();
        } else if (event.getFullCommandName().contains("playingnow")) {
            event.reply("**Playing now: **" + trackScheduler.playingNow()).queue();
        }else if (event.getFullCommandName().contains("shuffle")) {
            event.deferReply().queue();
            if (tracks.isEmpty()) {
                event.getHook().sendMessage("There are no tracks in the queue to shuffle.").queue();
            } else {
                trackScheduler.shuffleQueue();
                event.getHook().sendMessage("The queue has been shuffled!").queue();
            }
        }else if (event.getFullCommandName().contains("register")){
            event.deferReply().queue();
            String username = event.getOption("username").getAsString();
            String password = event.getOption("password").getAsString();
            boolean registersuccess = Api.registerUser(username,password);
            if(registersuccess == true){
                event.getHook().sendMessage("User registered successfully! \n **Welcome: " + username + "**").queue();
            }else{
                event.getHook().sendMessage("An error ocurred while registering!.").queue();
            }


        }

    }

    public void onGuildReady(GuildReadyEvent event) {
        //Variable type List to store all commands
        List<CommandData> commandList = new ArrayList<>();
        //Adding commands to the list
        OptionData option1 = new OptionData(OptionType.STRING, "song", "link your song", true);
        commandList.add(Commands.slash("play", "Play a song").addOptions(option1));
        OptionData option3 = new OptionData(OptionType.INTEGER,"count","Set how many tracks do u want to skip");
        commandList.add(Commands.slash("skip", "Skip the current song").addOptions(option3));
        commandList.add(Commands.slash("playpause", "Play / Pause the current track"));
        OptionData option2 = new OptionData(OptionType.INTEGER, "volume", "Set the volume of the bot. 0-100", true);
        OptionData username = new OptionData(OptionType.STRING, "username","Set the username of your account",true);
        OptionData password = new OptionData(OptionType.STRING, "password","Type your password for the account",true);
        commandList.add(Commands.slash("volume", "set a volume!").addOptions(option2));
        commandList.add(Commands.slash("queue", "List all tracks in queue"));
        commandList.add(Commands.slash("clear", "Clear all tracks in the queue"));
        commandList.add(Commands.slash("disconnect", "disconnect bot from the VC"));
        commandList.add(Commands.slash("playingnow", "Shows track that is playing now"));
        commandList.add(Commands.slash("shuffle","Randomize the queue"));
        commandList.add(Commands.slash("register" ,"Register your user in the database").addOptions(username,password));
        //Registering commandList to JDA API (bot)
        event.getGuild().updateCommands().addCommands(commandList).queue();


    }
}
