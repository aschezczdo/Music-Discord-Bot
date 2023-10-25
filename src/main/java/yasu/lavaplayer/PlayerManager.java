package yasu.lavaplayer;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import yasu.Main;


import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
This class is responsible for managing the audio players for each guild in your Discord server.
 */
public class PlayerManager {

    public static PlayerManager INSTANCE; //It creates one PlayerManager Instance. Only one holds all programm
    private final Map<Long, GuildMusicManager> musicManagers; //Maps each guild to the corresponding "GuildMusicManager" Object. (GuildMusicManager class)
    private final AudioPlayerManager audioPlayerManager; //Manages the creation of audio players. AudioPlayerManager is a class from lavaplayer.
    private Map<Guild, List<AudioTrack>> loadedTracks = new HashMap<>();
    //private static final Logger logger = LoggerFactory.getLogger(CmdPlay.class);
    public static String guildId = "1074309101787557909";


    //Constructor of the class
    public PlayerManager() {
        //logger.info("Initializing PlayerManager...");
        this.musicManagers = new HashMap<>();
        this.audioPlayerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(this.audioPlayerManager);  //Registering supported remote sources
        AudioSourceManagers.registerLocalSource(this.audioPlayerManager);   //Registering supported local sources.
        //logger.info("PlayerManager initialized.");
    }
    public static synchronized PlayerManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PlayerManager();
        }
        return INSTANCE;
    }

    /*
    This method takes a Guild object as input and returns the corresponding GuildMusicManager object from the musicManagers map.
    If the GuildMusicManager object does not exist for the given guild, it creates a new one, sets the sending handler of the guild's
    audio manager to the send handler of the GuildMusicManager, and returns the new GuildMusicManager object.
     */
    public GuildMusicManager getMusicManager(Guild guild) {
        //logger.info("Fetching GuildMusicManager for Guild ID: " + guild.getId());
        return this.musicManagers.computeIfAbsent(guild.getIdLong(), (guildId) -> {
            final GuildMusicManager guildMusicManager = new GuildMusicManager(this.audioPlayerManager);
            guild.getAudioManager().setSendingHandler(guildMusicManager.getSendHandler());
            return guildMusicManager;
        });
    }
    public void loadAndPlay(TextChannel textChannel, String trackURL, SlashCommandInteractionEvent event) {
        //logger.info("Loading and playing track from URL: " + trackURL);
        final GuildMusicManager musicManager = this.getMusicManager(textChannel.getGuild());

        this.audioPlayerManager.loadItemOrdered(musicManager, trackURL, new AudioLoadResultHandler() { //loads the audio track or playlist and passes it to an AudioLoadResultHandler.
            @Override
            public void trackLoaded(AudioTrack audioTrack) {
                musicManager.scheduler.queue(audioTrack);
                if (musicManager.scheduler.getQueue().size() == 0) {
                    event.getHook().sendMessage("**Playing now: **" + audioTrack.getInfo().title + " **by ** " + audioTrack.getInfo().author).queue();

                } else {
                    event.getHook().sendMessage("**Adding to queue: ** " + audioTrack.getInfo().title + " **by ** " + audioTrack.getInfo().author).queue();

                }
            }

            /*
            This method is related with the search function.
            Example: /play <nametrack>  --> it will go to Youtube will search for <nametrack> and will take the first track of the search.
             */
            @Override
            public void playlistLoaded(AudioPlaylist audioPlaylist) {
                final List<AudioTrack> tracks = audioPlaylist.getTracks();

                // Check if it's a search result or a playlist
                if (audioPlaylist.isSearchResult()) {
                    if (!tracks.isEmpty()) {
                        musicManager.scheduler.queue(tracks.get(0));
                        event.getHook().sendMessage("Adding to queue: " + tracks.get(0).getInfo().title + " **by ** " + tracks.get(0).getInfo().author).queue();
                    }
                } else { // It's a playlist
                    for (AudioTrack track : tracks) {
                        musicManager.scheduler.queue(track);
                        try {
                            Thread.sleep(500); // 500 milliseconds = 0.5 seconds
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    event.getHook().sendMessage("Added " + tracks.size() + " tracks from the playlist to the queue.").queue();
                }
            }

            @Override
            public void noMatches() {
                event.getHook().sendMessage("No matches found for the provided input.").queue();

            }

            @Override
            public void loadFailed(FriendlyException e) {
                event.getHook().sendMessage("Failed to load: " + e.getMessage()).queue();
                event.getHook().sendMessage("Failed to load");
            }
        });

    }


    public static PlayerManager getINSTANCE() {
        if (INSTANCE == null) {
            INSTANCE = new PlayerManager();

        }
        return INSTANCE;
    }


}
