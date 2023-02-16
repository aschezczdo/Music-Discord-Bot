package yasu.lavaplayer;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
// Source code recreated from a .class file by IntelliJ IDEA



public class ExecuteArgs {
    private final Channel channel;
    //private final TextChannel textChannel;
    private final Member selfMember;
    private final Member member;
    private final Guild guild;
    private final JDA jda;
    private final Message message;
    private final String[] args;
    private final GuildVoiceState selfVoiceState;
    private final GuildVoiceState memberVoiceState;

    protected ExecuteArgs(MessageReceivedEvent event) {
        //this.textChannel = event.getTextChannel();
        this.member = event.getMember();
        this.guild = event.getGuild();
        this.jda = event.getJDA();
        this.message = event.getMessage();
        this.selfMember = this.guild.getSelfMember();
        this.args = this.message.getContentRaw().split(" ");
        this.selfVoiceState = this.selfMember.getVoiceState();
        this.memberVoiceState = this.member.getVoiceState();
        this.channel = event.getChannel();
    }
    /*
    public TextChannel getTextChannel() {
        return this.textChannel;
    }
    */

    public Channel getChannel(){
        return this.channel;
    }

    public Member getSelfMember() {
        return this.selfMember;
    }

    public Member getMember() {
        return this.member;
    }

    public Guild getGuild() {
        return this.guild;
    }

    public JDA getJda() {
        return this.jda;
    }

    public Message getMessage() {
        return this.message;
    }

    public String[] getArgs() {
        return this.args;
    }

    public GuildVoiceState getSelfVoiceState() {
        return this.selfVoiceState;
    }

    public GuildVoiceState getMemberVoiceState() {
        return this.memberVoiceState;
    }
}

