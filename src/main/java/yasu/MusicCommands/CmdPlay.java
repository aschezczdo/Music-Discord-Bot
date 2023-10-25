package yasu.MusicCommands;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.managers.AudioManager;
import yasu.lavaplayer.PlayerManager;
import yasu.lavaplayer.TrackScheduler;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CmdPlay extends ListenerAdapter {
    //private static final Logger logger = LoggerFactory.getLogger(CmdPlay.class);
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private ScheduledFuture<?> scheduledTask;

    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) { //Listens for all "/" events
        //logger.info("Received SlashCommandInteractionEvent: " + event.getName());
        if (!event.getName().equals("play")) { //Checking if event = "play"
            return; //If not, we ignore
        }
        event.deferReply().queue();
        Guild guild = event.getGuild();
        if (!event.getMember().getVoiceState().inAudioChannel()) { //Checks if use its in a voice channel
            //logger.warn("Member not in voice channel.");
            event.getHook().sendMessage("You need to be in a voice channel to use this command").queue();
            return;
        }
        if (!guild.getSelfMember().getVoiceState().inAudioChannel()) { //We check if bot is already in a voice channel, if not we connect it to the VC
            final AudioManager audioManager = event.getGuild().getAudioManager();
            final VoiceChannel memberChannel = (VoiceChannel) event.getMember().getVoiceState().getChannel();
            audioManager.openAudioConnection(memberChannel);
        }
        List<OptionMapping> option1 = event.getOptions(); //We store the Link passed by the user
        String link = option1.get(0).getAsString();
        if (!isUrl(link)) {
            link = "ytsearch:" + link + "audio";
        }
        TextChannel textChannel = event.getChannel().asTextChannel();
        PlayerManager.getINSTANCE().loadAndPlay(textChannel, link, event); //We load the link provided to the loadAndPlay method
    }

    public boolean isUrl(String url) {
        try {
            new URI(url);
            return true;
        } catch (URISyntaxException e) {
            return false;
        }
    }

    //Auto disconnects bot if it's alone
    @Override
    public void onGuildVoiceUpdate(GuildVoiceUpdateEvent event) {
        TrackScheduler trackScheduler = PlayerManager.getINSTANCE().getMusicManager(event.getGuild()).scheduler;
        if (Objects.requireNonNull(event.getGuild().getSelfMember().getVoiceState()).inAudioChannel()) {
            if (event.getGuild().getSelfMember().getVoiceState().getChannel().getMembers().size() == 1) {
                if (scheduledTask != null && !scheduledTask.isDone()) {
                    scheduledTask.cancel(true);
                }
                scheduledTask = scheduler.schedule(() -> {
                    trackScheduler.clearQueue();
                    event.getGuild().getAudioManager().closeAudioConnection();
                }, 10, TimeUnit.SECONDS); // Waits for 10 seconds before executing the task.
            } else {
                if (scheduledTask != null && !scheduledTask.isDone()) {
                    scheduledTask.cancel(true);
                }
            }
        }
    }
}