package yasu;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.managers.AudioManager;
import yasu.lavaplayer.PlayerManager;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class CmdPlay extends ListenerAdapter {
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event){
        Guild guild = event.getGuild();

        if(! event.getMember().getVoiceState().inAudioChannel()){
            event.reply("You need to be in a voice channel to use this command");
            return;
        }
        if(! guild.getSelfMember().getVoiceState().inAudioChannel()){
            final AudioManager audioManager = event.getGuild().getAudioManager();
            final VoiceChannel memberChannel = (VoiceChannel) event.getMember().getVoiceState().getChannel();

            audioManager.openAudioConnection(memberChannel);

        }
        List<OptionMapping> option1= event.getOptions();
        String link = option1.get(0).getAsString();

        TextChannel textChannel = event.getChannel().asTextChannel();
        PlayerManager.getINSTANCE().loadAndPlay(textChannel,link);

        }
        /*
        This method might be used only if we want to do a searcher instead of adding url
         */
    public boolean isUrl(String url) {
        try {
            new URI(url);
            return true;
        } catch (URISyntaxException e) {
            return false;
        }
    }

    public String getName(){
        return "play";
    }
    public String helpMessage(){
        return "This command is to play any track from youtube in a discord voice channel";
    }
    public String error1() {
        return "You have to be in a voice channel";
    }
    public boolean needOwner(){
        return false;
    }
}
