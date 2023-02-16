package yasu.lavaplayer;

import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.w3c.dom.Text;

import java.awt.*;
import java.util.ArrayList;

public class Listener extends ListenerAdapter {

    public static TextChannel getChannel(MessageReceivedEvent event) {
        return (TextChannel) event.getChannel();
    }

}