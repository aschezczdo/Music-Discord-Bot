package yasu.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.Header;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import org.json.JSONObject;
import yasu.Main;
import yasu.lavaplayer.GuildMusicManager;
import yasu.lavaplayer.PlayerManager;
import yasu.lavaplayer.Song;
import yasu.lavaplayer.TrackScheduler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Api {

    private static Javalin app;
    public static String guildId2 = "1074309101787557909";
    public static void startApi() {
        System.out.println("API Started");

        // Enabling CORS
        app = Javalin.create(config -> {
            config.plugins.enableCors(cors -> {
                cors.add(it -> {
                    it.anyHost();
                });
            });
        }).start("0.0.0.0", 7000);

        app.post("/bot/playpause", Api::handlePlayPauseCommand);
        app.get("/bot/status", Api::returnStatus);
        app.get("/bot/queue", Api::fetchQueue);
        app.post("/bot/prevtrack", Api::previousTrack);
        app.post("bot/nextsong",Api::nextTrack);
        app.delete("bot/queue", Api::deleteQueue);
    }

    public static void deleteQueue(Context ctx) {
        JSONObject jo = new JSONObject(
                ctx.body()
        );
        int position = jo.getInt("position");

        System.out.println(position);

        JDA botInstance = Main.bot;
        Guild guild = botInstance.getGuildById(guildId2);
        TrackScheduler trackScheduler = PlayerManager.getINSTANCE().getMusicManager(guild).scheduler;
        trackScheduler.removeTrack(position);

    }

    public static void fetchQueue(Context ctx) {
        JDA botInstance = Main.bot;
        // Extract the guild ID or any other necessary information from the request
        Guild guild = botInstance.getGuildById(guildId2);

        // Fetch the tracks
        List<AudioTrack> tracks = PlayerManager.getINSTANCE().getMusicManager(guild).scheduler.getQueue();

        // Convert the tracks to a structured format that can be converted to JSON
        List<Map<String, Object>> trackList = new ArrayList<>();

        for (int i = 0; i < tracks.size(); i++) {
            AudioTrack track = tracks.get(i);
            AudioTrackInfo info = track.getInfo();

            Map<String, Object> trackData = new HashMap<>();
            trackData.put("position", i + 1);
            trackData.put("title", info.title);
            trackData.put("author", info.author);
            trackData.put("uri", info.uri); // Assuming you might also want the track's URI

            trackList.add(trackData);
        }

        // Convert the structured data to JSON and send as a response
        ctx.json(trackList);
    }


    private static void handlePlayPauseCommand(Context ctx) {
        JDA botInstance = Main.bot;
        // Extract the guild ID or any other necessary information from the request
        Guild guild = botInstance.getGuildById(guildId2);

        // Use the logic you provided for play/pause command:
        TrackScheduler trackScheduler = PlayerManager.getINSTANCE().getMusicManager(guild).scheduler;

        if (trackScheduler.audioPlayer.isPaused()) {
            trackScheduler.audioPlayer.setPaused(false);
            ctx.result("Track had been resumed");
        } else {
            trackScheduler.audioPlayer.setPaused(true);
            ctx.result("Track had been paused");
        }
    }

    private static void returnStatus(Context ctx) {
        System.out.println("handlePlayPauseCommand invoked");
        JDA botInstance = Main.bot;
        // Extract the guild ID or any other necessary information from the request
        Guild guild = botInstance.getGuildById(guildId2);

        // Use the logic you provided for play/pause command:
        TrackScheduler trackScheduler = PlayerManager.getINSTANCE().getMusicManager(guild).scheduler;

        boolean paused = trackScheduler.audioPlayer.isPaused();

        ctx.result(paused ? "PAUSED" : "PLAYING");
    }

    /*
        public void nextTrack() {
        this.audioPlayer.startTrack(this.queue.poll(), false);
        audioPlayer.getPlayingTrack();
    }
     */

    public static void previousTrack(Context ctx) {
        System.out.println("previoustrack invoked");
        JDA botInstance = Main.bot;
        Guild guild = botInstance.getGuildById(guildId2);
        final GuildMusicManager musicManager = PlayerManager.getINSTANCE().getMusicManager(guild);
        final AudioPlayer audioPlayer = musicManager.audioPlayer;
        musicManager.scheduler.prevTrack();

        if(!musicManager.scheduler.queue.isEmpty()){
            ctx.result("Successfully changed the track");

        }else{
            ctx.result("Queue is empty");

        }
        ctx.status(200).result("Track processed successfully");



        ctx.status(400).result("Invalid JSON format provided or an error occurred while playing the track");

    }
    public static void nextTrack(Context ctx){
        JDA botInstance = Main.bot;
        Guild guild = botInstance.getGuildById(guildId2);
        final GuildMusicManager musicManager = PlayerManager.getINSTANCE().getMusicManager(guild);
        final AudioPlayer audioPlayer = musicManager.audioPlayer;
        musicManager.scheduler.nextTrack();
        if(!musicManager.scheduler.queue.isEmpty()){
            ctx.result("Successfully changed the track");
        }else{
            ctx.result("Queue is empty");
        }


    }

}

