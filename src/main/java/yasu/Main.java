package yasu;

import ca.tristan.jdacommands.JDACommands;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import yasu.lavaplayer.CmdPlay;
import yasu.lavaplayer.Config;

public class Main {

    public static void main(String args[]) throws InterruptedException {
        try {
            // Creating the bot (default config)x
            JDA bot = JDABuilder.createDefault(Config.getDiscordApiToken())
                    .setActivity(Activity.playing("with your mom"))
                    .setStatus(OnlineStatus.ONLINE)
                    .enableCache(CacheFlag.VOICE_STATE)
                    .enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_WEBHOOKS, GatewayIntent.MESSAGE_CONTENT)
                    .addEventListeners(new CmdPlay())
                    .build().awaitReady(); // await.Ready() is really important for guild commands

            // Type of commands: Global commands and Guild Commands
            // Global commands: they can be used anywhere: any guild that ur bot in and also
            // in DM's
            // Guild commands: They can only be used in a specific guild
            bot.upsertCommand("play","play a song");


        } catch (Exception e) {
            System.out.println("Wrong discord token");
        }

    }
}