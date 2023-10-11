Music Discord Bot made in Java. 

This bot had been developed with JDA 5.0 using lavaplayer for audio stuff.

Play your own music from any of this providers:
*YouTube
*SoundCloud
*Bandcamp
*Vimeo
*Twitch streams
*Local files
*HTTP URLs

It has some Music Commands such as:
* /Play <url> OR <SongName> --> Play any track. It automatically add it to a queue if there're already any track being played
* /Skip <amount> --> Skip to the next track
* /Disconnect --> Disconnect bot from any VC
* /Playpause --> Play / Pause the current track
* /Clear --> Clear the tracks on queue
* /volume <Int Value> --> Set the volume of the audioplayer. Value from 0-10
* /queue --> Shows the tracks in queue
* /playingnow --> Shows the track that is being played
* /shuffle --> Randomize the queue
**Playlists are supported!**

`--HOW TO SETUP--`
1. Download the .jar from releases 
2. Make a config.properties file on the same folder that you have the .jar -> Be sure the file is named exactly config.properties 
3. Inside config.properties just add discord_api_key=INSERT_YOUR_API_KEY 
4. Just doble click on the jar once u added your api key and there you go!

`--REQUERIMENTS--`
-Have installed JDA 19.0 and added into your enviroment variables.
