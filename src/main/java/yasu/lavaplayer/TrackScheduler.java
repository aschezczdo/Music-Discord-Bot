package yasu.lavaplayer;

import com.sedmelluq.discord.lavaplayer.player.*;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.w3c.dom.Text;
import yasu.Main;

import javax.sound.midi.Track;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/*
This class is  used to manage the queue and play tracks.
It has two parameters. AudioPlayer & Queue.
 */
public class    TrackScheduler extends AudioEventAdapter {
    public final AudioPlayer audioPlayer; //Audio reproductor

    public final BlockingQueue<AudioTrack> queue; //Queue of song
    private AudioTrack lastPlayedTrack; // This variable will store the last played track

    //Constructor with one parameter.
    //It initializes the queue variable as "LinkedBlockinDeque
    public TrackScheduler(AudioPlayer audioPlayer) {
        this.audioPlayer = audioPlayer;
        this.queue = new LinkedBlockingDeque<>();
    }

    //This method adds one track to the queue. If any track is being played, it play the track inmediately.
    public void queue(AudioTrack track) {
        // Save the currently playing track as the last played one
        if (this.audioPlayer.getPlayingTrack() != null) {
            this.lastPlayedTrack = this.audioPlayer.getPlayingTrack();
        }

        if (!this.audioPlayer.startTrack(track, true)) {
            this.queue.offer(track);
        }
    }
    /*
    This method runs the following track in the queue.
    First it removes the current song from the queue using method .poll()
    Then it starts the track using the method .startTrack with false argument that means the current song gets interrupted to play the next one
     */
    public void nextTrack() {
        if (this.audioPlayer.getPlayingTrack() != null) {
            this.lastPlayedTrack = this.audioPlayer.getPlayingTrack();
        }
        this.audioPlayer.startTrack(this.queue.poll(), false);
        audioPlayer.getPlayingTrack();
    }
    public void prevTrack() {
        if (this.lastPlayedTrack != null) {
            // Cloning the track because the original might have its position set to the end
            AudioTrack clonedTrack = this.lastPlayedTrack.makeClone();
            this.audioPlayer.startTrack(clonedTrack, false);
        } else {
            // Handle case where there's no previous track to play
            System.out.println("No previous track found!");
        }
    }


    public void shuffleQueue() {
        List<AudioTrack> list = new ArrayList<>(queue);
        Collections.shuffle(list);
        queue.clear();
        queue.addAll(list);
    }



    public void setVolume(int i) {
        if (i > 0 && i < 100) {
            this.audioPlayer.setVolume(i);
        }
    }

    public int getVolume() {

        return audioPlayer.getVolume();
    }

    public List<AudioTrack> getQueue() {
        return new ArrayList<>(queue);
    }

    public void clearQueue() {
        this.queue.clear();
    }
    public void removeTrack(int position){
        JDA jda = Main.bot;
        String guildId = "1074309101787557909";
        Guild guild = jda.getGuildById(guildId);
        List<AudioTrack> tracks = PlayerManager.getINSTANCE().getMusicManager(guild).scheduler.getQueue();
        for (int i = 0; i < tracks.size(); i++) {
            AudioTrack track = tracks.get(i);
            AudioTrackInfo info = track.getInfo();

            if(i+1 == position)
            {
                this.queue.remove(track);
            }
        }
    }

    public String playingNow(){
        AudioTrack track = this.audioPlayer.getPlayingTrack();
        String playingTrack = track.getInfo().title;
        return playingTrack;
    }

    public void resumeTrack() {
        this.audioPlayer.setPaused(false);  //If there are already a track, it just setPause as false, so it continue;
    }
    @Override
    //This method will be called when a track is about to end from the AudioEventAdapter class
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        this.lastPlayedTrack = track;
        if(endReason.mayStartNext){
            nextTrack();
        }
    }
}
