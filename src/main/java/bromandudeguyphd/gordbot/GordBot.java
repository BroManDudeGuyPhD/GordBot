// Started April 9, 2020, 16:05:48 

package bromandudeguyphd.gordbot;

import com.samczsun.skype4j.Skype;
import com.samczsun.skype4j.SkypeBuilder;
import com.samczsun.skype4j.exceptions.ConnectionException;
import com.samczsun.skype4j.exceptions.InvalidCredentialsException;
import com.samczsun.skype4j.exceptions.NotParticipatingException;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.track.playback.NonAllocatingAudioFrameBuffer;
import discord4j.core.DiscordClient;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.VoiceState;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.User;
import discord4j.voice.AudioProvider;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author BroManDudeGuyPhD
 */


interface Command {
    // Since we are expecting to do reactive things in this method, like
    // send a message, then this method will also return a reactive type.
    Mono<Void> execute(MessageCreateEvent event);
}


public class GordBot {

    public static Skype skype;
    public static User root;
    private static final long startTime = System.currentTimeMillis();

    public static void main(String[] args){
        final Map<String, Command> commands = new HashMap<>();
           // Creates AudioPlayer instances and translates URLs to AudioTrack instances
        final AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
           // This is an optimization strategy that Discord4J can utilize. It is not important to understand
        playerManager.getConfiguration().setFrameBufferFactory(NonAllocatingAudioFrameBuffer::new);
            // Allow playerManager to parse remote sources like YouTube links
        AudioSourceManagers.registerRemoteSources(playerManager);
            // Create an AudioPlayer so Discord4J can receive audio data
        final AudioPlayer player = playerManager.createPlayer();
            // We will be creating LavaPlayerAudioProvider in the next step
        AudioProvider provider = new LavaPlayerAudioProvider(player);

        
    final DiscordClient gordbot = new DiscordClientBuilder(tokens.discordToken()).build();
        gordbot.getEventDispatcher().on(ReadyEvent.class)
            .subscribe(ready -> System.out.println("Logged in as " + ready.getSelf().getUsername()));

        
        commands.put("commands", event -> event.getMessage().getChannel() 
            .flatMap(channel -> channel.createMessage("Not much here yet!"))
            .then());
        
        
        commands.put("ping", event -> event.getMessage().getChannel()
            .flatMap(channel -> channel.createMessage("Pong!"))
            .then());
        

        
        
//Music Commands
        commands.put("join", event -> Mono.justOrEmpty(event.getMember())
            .flatMap(Member::getVoiceState)
            .flatMap(VoiceState::getChannel)
            // join returns a VoiceConnection which would be required if we were
            // adding disconnection features, but for now we are just ignoring it.
            .flatMap(channel -> channel.join(spec -> spec.setProvider(provider)))
            .then(event.getMessage().delete())
            .then());
        
        
        
        final TrackScheduler scheduler = new TrackScheduler(player);
        
        commands.put("play", event -> Mono.justOrEmpty(event.getMessage().getContent())
            .map(content -> Arrays.asList(content.split(" ")))
            .doOnNext(command -> playerManager.loadItem(command.get(1), scheduler))
            .then());
        
        
        
        gordbot.getEventDispatcher().on(MessageCreateEvent.class)
            .flatMap(event -> Mono.justOrEmpty(event.getMessage().getContent())
            .flatMap(content -> Flux.fromIterable(commands.entrySet())
            // We will be using ! as our "prefix" to any command in the system.
            .filter(entry -> content.startsWith('!' + entry.getKey()))
            .flatMap(entry -> entry.getValue().execute(event))
            .next()))
        .subscribe();
        
       
        

        //Where the bot logs on A.K.A ====> MUST RUN LAST <====
        gordbot.login().block();
        
    
//        try {
//            bootSkype();
//        }catch (InvalidCredentialsException | ConnectionException | NotParticipatingException ignored){}
    }


    public static void bootSkype() throws InvalidCredentialsException, ConnectionException, NotParticipatingException {
        String password = tokens.skypePassword();
        skype = new SkypeBuilder("sirbrobot", password).withAllResources().build();
        skype.login();

//        skype.getEventDispatcher().registerListener(new Listener() {
//            @EventHandler
//            public void onMessage(MessageReceivedEvent e) {
//                System.out.println("Got message: " + e.getMessage().getContent());
//                //IMessage sendMessage = client.getOrCreatePMChannel(root).sendMessage("Recieved Skype Message: "+e.getMessage().getContent().toString()+"\nFrom: "+e.getMessage().getSender().getDisplayName());
//
//                if (e.getMessage().getContent().toString().contains("?REBOOT")) {
//
//                    try {
//                        Thread.sleep(2000);
//                    } catch (InterruptedException ex) {
//                        Thread.currentThread().interrupt();
//                    }
//                    ProcessBuilder pb = new ProcessBuilder("java", "-jar", SirBroBot.class.getProtectionDomain().getCodeSource().getLocation().toString());
//                    pb.inheritIO();
//                    try {
//                        pb.start();
//                    } catch (IOException e1) {
//                        LOGGER.error("Could not reboot!", e1);
//                        try {
//                            e.getMessage().getSender().getChat().sendMessage("Could not reboot! " + e);
//                        } catch (ConnectionException e2) {
//                            LOGGER.error("Could not send skype message!", e2);
//                        }
//                        return;
//                    }
//                    System.exit(0);
//                }
//            }
//        });
//        skype.subscribe();
    }
        
        public static String getUptime() {
            
        long tEnd = System.currentTimeMillis();
        long tDelta = tEnd - startTime;

        int days = (int) TimeUnit.MILLISECONDS.toDays(tDelta);
        long hrs = TimeUnit.MILLISECONDS.toHours(tDelta) - (days * 24);
        long min = TimeUnit.MILLISECONDS.toMinutes(tDelta) - (TimeUnit.MILLISECONDS.toHours(tDelta) * 60);
        long sec = TimeUnit.MILLISECONDS.toSeconds(tDelta) - (TimeUnit.MILLISECONDS.toMinutes(tDelta) * 60);

        if(days != 00){
            return String.format("%02dd:%02dh:%02dm:%02ds", days, hrs, min, sec);
        }
        return String.format("%02dh:%02dm:%02ds", hrs, min, sec);
    }
        
        public static String getTimeFromMilis(long milis) {

        long hrs = TimeUnit.MILLISECONDS.toHours(milis);
        long min = TimeUnit.MILLISECONDS.toMinutes(milis) - (TimeUnit.MILLISECONDS.toHours(milis) * 60);
        long sec = TimeUnit.MILLISECONDS.toSeconds(milis) - (TimeUnit.MILLISECONDS.toMinutes(milis) * 60);

        return String.format("%02dh:%02dm:%02ds", hrs, min, sec);
    }
        
        public static String getDateTime(){
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            
            return dtf.format(now);
        }
        
    public static Integer getRand() {
        int Low = 0;
        int High = 10;

        int Result;
        Random r = new Random();
        Result = r.nextInt(High - Low) + Low;
        return Result;
    }

    }



