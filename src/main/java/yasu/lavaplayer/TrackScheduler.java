package yasu.lavaplayer;

import com.sedmelluq.discord.lavaplayer.player.*;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import net.dv8tion.jda.api.entities.Guild;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/*
This class is  used to manage the queue and play tracks.
It has two parameters. AudioPlayer & Queue.
 */
public class TrackScheduler extends AudioEventAdapter {
    private Guild guild;
    public final AudioPlayer audioPlayer; //Audio reproductor
    public final BlockingQueue<AudioTrack>queue; //Queue of song
    //Constructor with one parameter.
    //It initializes the queue variable as "LinkedBlockinDeque
    public TrackScheduler(AudioPlayer audioPlayer){
        this.audioPlayer = audioPlayer;
        this.queue = new LinkedBlockingDeque<>();
    }
    //This method adds one track to the queue. If any track is being played, it play the track inmediately.
    public void queue(AudioTrack track){
        //if there isn't being playing any track, it offers the next one on queue
        if(!this.audioPlayer.startTrack(track, true)) {
            this.queue.offer(track);
        }
    }

    /*
    This method runs the following track in the queue.
    First it removes the current song from the queue using method .poll()
    Then it starts the track using the method .startTrack with false argument that means the current song gets interrupted to play the next one
     */
    public void nextTrack(){

        this.audioPlayer.startTrack(this.queue.poll(),false);
        audioPlayer.getPlayingTrack();
    }
    public void pauseTrack(){

        this.audioPlayer.stopTrack();
    }
    public void setVolume(int i){
        this.audioPlayer.setVolume(i);
    }

    @Override
    //This method will be called when a track is about to end from the AudioEventAdapter class
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if(endReason.mayStartNext){
            nextTrack();
        }
    }
}
