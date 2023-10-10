package yasu.MusicCommands;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import yasu.lavaplayer.GuildMusicManager;
import yasu.lavaplayer.PlayerManager;

import java.util.List;

public class SkipCmd extends ListenerAdapter {
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        List<AudioTrack> tracks = PlayerManager.getINSTANCE().getMusicManager(event.getGuild()).scheduler.getQueue();
        final User self = event.getJDA().getSelfUser();
        if (event.getFullCommandName().equals("skip")) {
            event.deferReply().queue();
            final Member member = event.getMember();
            final GuildVoiceState memberVoiceState = event.getMember().getVoiceState();

            if (!memberVoiceState.inAudioChannel()) {
                event.getHook().sendMessage("You need to be in a voice channel for this command to work.").queue();
                return;
            }
            final GuildMusicManager musicManager = PlayerManager.getINSTANCE().getMusicManager(event.getGuild());
            final AudioPlayer audioPlayer = musicManager.audioPlayer;

            if (tracks.isEmpty()) {
                event.getHook().sendMessage("There is no track playing currently.").queue();
                return;
            }
            musicManager.scheduler.nextTrack();
            event.getHook().sendMessage("Skipped the current track. Now playing **`" + musicManager.audioPlayer.getPlayingTrack().getInfo().title + "`** by **`" + musicManager.audioPlayer.getPlayingTrack().getInfo().author + "`**.").queue();

        }
    }

}
