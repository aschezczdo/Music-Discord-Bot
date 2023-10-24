package yasu;


import io.javalin.Javalin;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import yasu.MusicCommands.AudioPlayerCommands;
import yasu.MusicCommands.CmdPlay;
import yasu.MusicCommands.SkipCmd;
import yasu.api.Api;
//import yasu.lavaplayer.CmdPlay;


public class Main {
    public static JDA bot;
    public static void main(String args[]) throws InterruptedException {
        try {
            // Creating the bot (default config)x
            bot = JDABuilder.createDefault(Config.getDiscordApiToken())
                    .setActivity(Activity.playing("with your mom"))
                    .setStatus(OnlineStatus.ONLINE)
                    .enableCache(CacheFlag.VOICE_STATE)
                    .enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_WEBHOOKS, GatewayIntent.MESSAGE_CONTENT)
                    .enableCache(CacheFlag.VOICE_STATE)
                    .addEventListeners(new AudioPlayerCommands())
                    .addEventListeners(new CmdPlay())
                    .addEventListeners(new SkipCmd())
                    .build().awaitReady(); // await.Ready() is really important for guild commands

            // Start the API
            Api.startApi();



        } catch (Exception e) {
            System.out.println("Wrong discord token");
        }

    }
}