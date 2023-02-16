package yasu.lavaplayer;

import ca.tristan.jdacommands.ExecuteArgs;
import ca.tristan.jdacommands.ICommand;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.managers.*;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import javax.print.URIException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;


public class CmdPlay implements ICommand {
    @Override
    public void execute(ExecuteArgs event) {
        Member member = event.getMember();
        if(!member.getVoiceState().inAudioChannel()){
            //event.getTextChannel().sendMessage("You");
        }
        if(!event.getSelfVoiceState().inAudioChannel()){
            final AudioManager audioManager = event.getGuild().getAudioManager();
            final VoiceChannel memberChannel = (VoiceChannel) event.getMemberVoiceState().getChannel();

            audioManager.openAudioConnection(memberChannel);
        }
        String link = String.join(" ", event.getArgs());

        if(!isUrl(link)){
            link = "ytsearch" + link + " audio";
        }
        //PlayerManager.getINSTANCE().loadAndPlay(event.getChannel(),link);
        PlayerManager.getINSTANCE().loadAndPlay(Listener.getChannel(),link);
    }
    public boolean isUrl(String url){
        try{
            new URI(url);
            return true;
        }catch(URISyntaxException e){
            return false;
        }
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String helpMessage() {
        return "This command is to play music ";
    }
    public String error1(){
        return "You have to be in a voice channel";
    }

    @Override
    public boolean needOwner() {
        return false;
    }
}
