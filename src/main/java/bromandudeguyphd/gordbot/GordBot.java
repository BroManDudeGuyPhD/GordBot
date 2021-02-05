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
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.guild.GuildCreateEvent;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.VoiceState;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.object.presence.Activity;
import discord4j.voice.AudioProvider;
import discord4j.core.object.presence.Presence;
import static discord4j.core.object.presence.Presence.online;
import discord4j.core.object.presence.Status;

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



import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClient;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.event.EventDispatcher;
import discord4j.core.event.domain.channel.PrivateChannelCreateEvent;
import discord4j.core.event.domain.guild.MemberJoinEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Message;
import discord4j.rest.util.Image;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


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
    static final long startTime = System.currentTimeMillis();

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

     //final DiscordClient gordbot = new DiscordClientBuilder(tokens.discordToken()).build();
     //   gordbot.getEventDispatcher().on(ReadyEvent.class)
     //       .subscribe(ready -> System.out.println("Logged in as " + ready.getSelf().getUsername()));
        
    final DiscordClient client = DiscordClient.create(tokens.discordToken());
    final GatewayDiscordClient gordbot = client.login().block();
    
        gordbot.getEventDispatcher().on(ReadyEvent.class)
        .subscribe(event -> {
            final User self = event.getSelf();
            System.out.println(String.format(
                "Logged in as %s#%s", self.getUsername(), self.getDiscriminator()
            ));
        });
        
        gordbot.getEventDispatcher().on(GuildCreateEvent.class)
        .subscribe(event -> {
            final Guild guild = event.getGuild();
            System.out.println("Joined Guild: "+ guild.getName() + "OwnerID: "+guild.getOwnerId());
            //event.getClient().updatePresence(Presence.online(Activity.playing("Writing a food blog")));
            //@BurneyProMod I am close to getting this, not sure what causes it to not update status on boot
        });
     
        gordbot.getEventDispatcher().on(MessageCreateEvent.class)
            .map(MessageCreateEvent::getMessage)
            .filter(message -> message.getAuthor().map(user -> !user.isBot()).orElse(false))
            .filter(message -> message.getContent().equalsIgnoreCase("!ping"))
            .flatMap(Message::getChannel)
            .flatMap(channel -> channel.createMessage("Pong!"))
            .subscribe();
        
//Bot is mentioned
        gordbot.getEventDispatcher().on(MessageCreateEvent.class)
            .map(MessageCreateEvent::getMessage)
            .filter(message -> message.getAuthor().map(user -> !user.isBot()).orElse(false))
            .filter(message -> message.getUserMentionIds().contains(gordbot.getSelfId()))
            .filter(message -> message.getContent().toLowerCase().contains("dance"))
            .flatMap(Message::getChannel)
            .flatMap(channel -> channel.createMessage("https://sirbrobot.com/images/dancingKnight.gif"))
            .subscribe();
    
//Commands
        commands.put("commands", event -> event.getMessage().getChannel() 
            .flatMap(channel -> channel.createMessage("Not much here yet!"))
            .filter(message -> message.getAuthor().map(user -> !user.isBot()).orElse(false))
            .then());
        
        commands.put("about", event -> event.getMessage().getChannel()
            .flatMap((MessageChannel channel) -> channel.createMessage(
                    "Users: "+gordbot.getGuilds().collectList().block().size()+"\n"+
                    "Servers: "+client.getGuilds().toStream().count()+"\n"+
                    "Uptime: "+CommandFunctions.getUptime(startTime)+"\n"+
                    ""))
            .then());
        
        commands.put("ping", event -> event.getMessage().getChannel()
            .flatMap(channel -> channel.createMessage("Pong!"))
            .then());
        
        commands.put("uptime", event -> event.getMessage().getChannel()
            .flatMap(channel -> channel.createMessage(CommandFunctions.getUptime(startTime)))
            .then());

//Music Commands
        commands.put("join", event -> Mono.justOrEmpty(event.getMember())
            .flatMap(Member::getVoiceState)
            .flatMap(VoiceState::getChannel)
            // join returns a VoiceConnection which would be required if we were
            // adding disconnection features, but for now we are just ignoring it.
            .flatMap(channel -> channel.join(spec -> spec.setProvider(provider)).and(event.getMessage().delete()))
            .then());
        
        
        
//ADMIN COMMANDS
        commands.put("shutdown",  event -> event.getMessage().getChannel()
            .flatMap(channel -> channel.createMessage("Goodbye!").and(event.getMessage().delete()))
            .filter(author -> event.getMessage().getAuthor().map(user -> user.getId().toString().equals("150074847546966017")).orElse(false))
            //.filter(message -> message.getAuthor().map(user -> !user.isBot()).orElse(false))
            .then(gordbot.logout())
            .then());
    
        
        
        commands.put("uptime", event -> event.getMessage().getChannel()
            .flatMap(channel -> channel.createMessage(CommandFunctions.getUptime(startTime)))
            .then());
        
        commands.put("say", event -> event.getMessage().getChannel()
            .flatMap(channel -> channel.createMessage(event.getMessage().getContent().replace(".say ", "")))
            .then(event.getMessage().delete()));
        
        commands.put("nickname", event -> event.getGuild()
            .flatMap(nickname -> nickname.changeSelfNickname(event.getMessage().getContent().replace(".nickname", "")))
            .then());
            
        commands.put("updatestatus", event -> event.getMessage().getChannel()
            .flatMap(channel -> channel.createMessage("Status updated!"))
            .then(gordbot.updatePresence(Presence.online(Activity.playing(event.getMessage().getContent().replace(".updatestatus", ""))))));
              


        
        
//        final AudioTrackScheduler scheduler = new AudioTrackScheduler(player);
//        
//        commands.put("play", event -> Mono.justOrEmpty(event.getMessage().getContent())
//            .map(content -> Arrays.asList(content.split(" ")))
//            .doOnNext(command -> playerManager.loadItem(command.get(1), scheduler))
//            .then());
        
        
        //Gets Commands by looking for stuff that starts with . and splitting  We will be using . as our "prefix" to any command in the system.
        gordbot.getEventDispatcher().on(MessageCreateEvent.class)
            .flatMap(event -> Mono.justOrEmpty(event.getMessage().getContent())
            .flatMap(content -> Flux.fromIterable(commands.entrySet())
            .filter(entry -> content.startsWith('.' + entry.getKey()))
            .flatMap(entry -> entry.getValue().execute(event))
            .next()))
        .subscribe();
        
        

//        try {
//            bootSkype();
//        }catch (InvalidCredentialsException | ConnectionException | NotParticipatingException ignored){}
    gordbot.onDisconnect().block();
    
    }//end of MAIN
    
    
    
    public static final AudioPlayerManager PLAYER_MANAGER;

    static {
        PLAYER_MANAGER = new DefaultAudioPlayerManager();
        // This is an optimization strategy that Discord4J can utilize to minimize allocations
        PLAYER_MANAGER.getConfiguration().setFrameBufferFactory(NonAllocatingAudioFrameBuffer::new);
        AudioSourceManagers.registerRemoteSources(PLAYER_MANAGER);
        AudioSourceManagers.registerLocalSource(PLAYER_MANAGER);
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
    
    
    }




