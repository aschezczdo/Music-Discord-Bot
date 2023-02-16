package yasu.lavaplayer;

import com.sedmelluq.discord.lavaplayer.player.*;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class TrackSheduler extends AudioEventAdapter {
    public final AudioPlayer audioPlayer; //Audio reproductor
    public final BlockingQueue<AudioTrack>queue; //Queue of songs
    //Constructor
    public TrackSheduler(AudioPlayer audioPlayer){
        this.audioPlayer = audioPlayer;
        this.queue = new LinkedBlockingDeque<>();
    }
    public void queue(AudioTrack track){
        //if there isn't being playing any track, it offers the next one on queue
        if(!this.audioPlayer.startTrack(track, true)) {
            this.queue.offer(track);
        }
    }
    public void nextTrack(){
        this.audioPlayer.startTrack(this.queue.poll(),false);
    }
    @Override
    //This method is gonna be called when a track is about to end from the AudioEventAdapter class
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if(endReason.mayStartNext){
            nextTrack();
        }
    }
}
