package yasu.lavaplayer;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.MutableAudioFrame;
import net.dv8tion.jda.api.audio.AudioSendHandler;

import java.nio.Buffer;
import java.nio.ByteBuffer;

//AudioSendHandler class is a class implemented by JDA Discord API and is used to manage the transmission of audio in a discord voice channel
public class AudioPlayerSendHandler implements AudioSendHandler {
    private final AudioPlayer audioPlayer; //Creating object AudioPlayer
    private ByteBuffer buffer; //Creating object ByteBuffer
    private MutableAudioFrame frame; //Creating MutableAudioFrame Object
    //Constructor with one parameter
    public AudioPlayerSendHandler(AudioPlayer audioPlayer){
        this.audioPlayer = audioPlayer; //Gets value when invoked
        this.buffer = ByteBuffer.allocate(1024); //Setting up the size of the buffer. 1024 bytes
        this.frame = new MutableAudioFrame(); //Initializing frame variable. MutableAudioFrame is used to send data from an audio to a channel voice
        this.frame.setBuffer(buffer); //Configuration of the frame. It means all audio-data sent will be stored on the ByteBuffer object
    }
    @Override
    //Opus is a data-audio format. Method checks if is Opus format.
    public boolean isOpus() {

        return true;
    }

    @Override
    //This method just checks if there are audio-data available to send to the VC.
    //It invokes audioPlayer.provide method from the lavaplayer library.
    public boolean canProvide() {

        return this.audioPlayer.provide(frame);
    }

    @Override
    //This method provides 20ms of audio stored in ByteBuffer object.
    //ByteBuffer is an object that represents a space on the memory to store data. In this case audio data
    public ByteBuffer provide20MsAudio() {
        final Buffer buffer = ((Buffer) this.buffer).flip(); //Flip is used to invert the view. So it starts from the end and it goes to the start. This is because while the audio data is generated, it's being added at the end.
        return (ByteBuffer) buffer;
    }
}
