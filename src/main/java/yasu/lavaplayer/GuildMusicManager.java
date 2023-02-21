package yasu.lavaplayer;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import java.util.ArrayList;
import java.util.List;

/*
The GuildMusicManager class is responsible for managing music playback on a particular Discord server.
It has 3 attributes: audioplayer, scheduler and sendhandler
 */
public class GuildMusicManager {
    public final AudioPlayer audioPlayer; //AudiPlayer Object, responsible of playing the songs
    public final TrackScheduler scheduler; //TrackScheduler object, responsible of the queue of tracks
    private final AudioPlayerSendHandler sendHandler; // Responsible of sending the audio-data to a voice channel
    /*
    TO CLARIFY:
    -AudioPlayer is a class that gives all the needed methods to play audio in a discord bot
    -AudioPlayerManager is a class that creates and manages instances of AudioPlayer
    */
    //Constructor with one parameter. 4 Attributes
    public GuildMusicManager(AudioPlayerManager manager){ //Class from the lavaplayer library
        this.audioPlayer = manager.createPlayer(); //Creating the AudioPlayer
        this.scheduler = new TrackScheduler(audioPlayer); //Creating scheduler variable
        this.audioPlayer.addListener(scheduler); //Setting scheduler as listener. It listens for "onTrackEnd" of AudioPlayer
        this.sendHandler = new AudioPlayerSendHandler(this.audioPlayer);
    }
    /*
    Returns sendHandler.
    This method is used to send audio data to the voice channel
     */
    public AudioPlayerSendHandler getSendHandler() {

        return this.sendHandler;
    }
}
