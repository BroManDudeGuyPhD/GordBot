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

import bromandudeguyphd.gordbot.music.LavaPlayerAudioProvider;
import bromandudeguyphd.gordbot.music.TrackScheduler;
import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClient;
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
import discord4j.discordjson.json.ApplicationInfoData;
import discord4j.discordjson.json.UserData;
import discord4j.rest.util.Color;

import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author BroManDudeGuyPhD
 */

interface Command {
    Mono<Void> execute(MessageCreateEvent event);
}

public class GordBot {
        private static final AtomicLong OWNER_ID = new AtomicLong();
        public static final String COMMAND_PREFIX = ".";
        public static Skype skype;
        public static User root;
        static final long startTime = System.currentTimeMillis();

    public static void main(String[] args) {
        final Map<String, Command> commands = new HashMap<>();
       
       
        // Creates AudioPlayer instances and translates URLs to AudioTrack instances
        final AudioPlayerManager playerManager = new DefaultAudioPlayerManager();

        // This is an optimization strategy that Discord4J can utilize.
        // It is not important to understand
        playerManager.getConfiguration().setFrameBufferFactory(NonAllocatingAudioFrameBuffer::new);

        // Allow playerManager to parse remote sources like YouTube links
        AudioSourceManagers.registerRemoteSources(playerManager);

        // Create an AudioPlayer so Discord4J can receive audio data
        final AudioPlayer player = playerManager.createPlayer();

        // We will be creating LavaPlayerAudioProvider in the next step
        AudioProvider provider = new LavaPlayerAudioProvider(player);


        final DiscordClient client = DiscordClient.create(tokens.discordToken());
        final GatewayDiscordClient gordbot = client.login().block();

        gordbot.getEventDispatcher().on(ReadyEvent.class).subscribe(event -> {
            final User self = event.getSelf();
            System.out.println(String.format("Logged in as %s#%s", self.getUsername(), self.getDiscriminator()));
            gordbot.updatePresence(Presence.online(Activity.streaming("Writing a food blog", "https://www.twitch.tv/SirBroBot/videos"))).block();

            client.getApplicationInfo()
                .map(ApplicationInfoData::owner)
                .map(UserData::id)
                .map(Snowflake::asLong)
                .doOnNext(ownerId -> {
                    System.out.println("Owner ID acquired: "+ ownerId);
                    GordBot.OWNER_ID.set(ownerId);
                })
                .block();
        });

        gordbot.getEventDispatcher().on(GuildCreateEvent.class).subscribe(event -> {
            final Guild guild = event.getGuild();
            System.out.println("Joined Guild: " + guild.getName());
 
        });

        // gordbot.getEventDispatcher().on(MessageCreateEvent.class)
        // .map(MessageCreateEvent::getMessage)
        // .filter(message -> message.getAuthor().map(user ->
        // !user.isBot()).orElse(false))
        // .filter(message -> message.getContent().equalsIgnoreCase("!ping"))
        // .flatMap(Message::getChannel)
        // .flatMap(channel -> channel.createMessage("Pong!"))
        // .subscribe();

        // Bot is mentioned
        // Bot is mentioned

        Map<String, String> mentionMap = new HashMap<String, String>();
        mentionMap.put("dance", "https://sirbrobot.com/images/dancingKnight.gif");
        mentionMap.put("test", "Test PASSED!");

        gordbot.getEventDispatcher().on(MessageCreateEvent.class)
                .map(MessageCreateEvent::getMessage)
                .filter(message -> message.getAuthor().map(user -> !user.isBot()).orElse(false))
                .filter(message -> message.getUserMentionIds().contains(gordbot.getSelfId()))
                .filter(message -> message.getContent().toLowerCase().contains("dance"))
                //.map(mentionContent -> checkMention(mentionContent))

                .flatMap(Message::getChannel)
                .flatMap(channel -> channel.createMessage("https://sirbrobot.com/images/dancingKnight.gif"))

                .subscribe();

        // Commands
        commands.put("commands",
                event -> event.getMessage().getChannel().flatMap(channel -> channel.createEmbed(spec -> spec.setColor(Color.RED)
                .setTitle("GordBot commands").setUrl("https://www.youtube.com/watch?v=Gc2u6AFImn8")

                .addField(GordBot.COMMAND_PREFIX+"commands", "See this list", false)
                .addField(GordBot.COMMAND_PREFIX+"about", "See bot stats", false)
                .addField(GordBot.COMMAND_PREFIX+"uptime", "See uptime", false)
                .addField(GordBot.COMMAND_PREFIX+"join", "Bot joins current voice channel", false)
                .addField(GordBot.COMMAND_PREFIX+"d#", "rolls # sided die", false)
                .setTimestamp(Instant.now()))).then());

        commands.put("ocommands",
                event -> event.getMessage().getChannel().flatMap(channel -> channel.createEmbed(spec -> spec.setColor(Color.RED)
                .setTitle("GordBot OWNER commands").setUrl("https://www.youtube.com/watch?v=Gc2u6AFImn8")

                .addField(GordBot.COMMAND_PREFIX+"ocommands", "See this list", false)
                .addField(GordBot.COMMAND_PREFIX+"shutdown", "What do you think", false)
                .addField(GordBot.COMMAND_PREFIX+"say", "Bot will say something", false)
                .addField(GordBot.COMMAND_PREFIX+"nick", "Change bot's nickname on server", false)
                .addField(GordBot.COMMAND_PREFIX+"updatestatus", "Change bot status", false)
                .setTimestamp(Instant.now()))).then());


        commands.put("about",
                event -> event.getMessage().getChannel()
                        .flatMap((MessageChannel channel) -> channel
                                .createMessage("Users: " + gordbot.getGuilds().collectList().block().size() + "\n"
                                        + "Servers: " + client.getGuilds().toStream().count() + "\n" + "Uptime: "
                                        + CommandFunctions.getUptime(startTime) + "\n" + ""))
                        .then());

        commands.put("embed", event -> event.getMessage().getChannel()
                .flatMap(channel -> channel.createEmbed(spec -> spec.setColor(Color.RED)
                        .setAuthor("setAuthor", "https://www.youtube.com/watch?v=Gc2u6AFImn8",
                                "https://cdn.betterttv.net/emote/55028cd2135896936880fdd7/3x")
                        .setImage("https://cdn.betterttv.net/emote/55028cd2135896936880fdd7/3x")
                        .setTitle("setTitle/setUrl").setUrl("https://www.youtube.com/watch?v=Gc2u6AFImn8")
                        .setDescription("setDescription\n" + "big D: is setImage\n" + "small D: is setThumbnail\n"
                                + "<-- setColor")
                        .addField("addField", "inline = true", true)
                        .addField("addFIeld", "inline = true", true)
                        .addField("addFile", "inline = false", false)
                        .setThumbnail("https://cdn.betterttv.net/emote/55028cd2135896936880fdd7/3x")
                        .setFooter("setFooter --> setTimestamp",
                                "https://cdn.betterttv.net/emote/55028cd2135896936880fdd7/3x")
                        .setTimestamp(Instant.now())))
                .then());

        commands.put("uptime", event -> event.getMessage().getChannel()
                .flatMap(channel -> channel.createMessage(CommandFunctions.getUptime(startTime))).then());

        commands.put("invite", event -> event.getMessage().getChannel().flatMap(channel -> channel.createMessage(
                "Invite me with: https://discord.com/oauth2/authorize?client_id=697886793739010111&scope=bot&permissions=8"))
                .then());

        commands.put("d", event -> event.getMessage().getChannel()
                .flatMap(channel -> channel.createMessage(CommandFunctions.dice(event.getMessage().getContent().split(GordBot.COMMAND_PREFIX+"d"))))
                .then(event.getMessage().delete()));


// ADMIN COMMANDS
        commands.put("shutdown",event -> event.getMessage().getChannel()
                        .flatMap(channel -> channel.createMessage("Goodbye!").and(event.getMessage().delete()))
                        .filter(author -> event.getMessage().getAuthor().get().getId().toString().equals(GordBot.OWNER_ID.toString()))
                                
                        // .filter(message -> message.getAuthor().map(user ->
                        // !user.isBot()).orElse(false))
                        .then(gordbot.logout()).then());

        commands.put("say",event -> event.getMessage().getChannel()
                        .flatMap(channel -> channel.createMessage(event.getMessage().getContent().replace(GordBot.COMMAND_PREFIX+"say ", "")))
                        .then(event.getMessage().delete()));

        commands.put("nickname", event -> event.getGuild().flatMap(
                nickname -> nickname.changeSelfNickname(event.getMessage().getContent().replace(GordBot.COMMAND_PREFIX+"nickname", "")))
                .then());

        commands.put("updatestatus", event -> event.getMessage().getChannel()
                .flatMap(channel -> channel.createMessage("Status updated!")).then(gordbot.updatePresence(Presence
                        .online(Activity.streaming(event.getMessage().getContent().replace(GordBot.COMMAND_PREFIX+"updatestatus", ""),"https://www.twitch.tv/SirBroBot/videos")))));



// Music Commands
        commands.put("join", event -> Mono.justOrEmpty(event.getMember()).flatMap(Member::getVoiceState)
                .flatMap(VoiceState::getChannel)
                // join returns a VoiceConnection which would be required if we were
                // adding disconnection features, but for now we are just ignoring it.
                .flatMap(channel -> channel.join(spec -> spec.setProvider(provider)).and(event.getMessage().delete()))
                .then());

        final TrackScheduler scheduler = new TrackScheduler(player);
        commands.put("play", event -> Mono.justOrEmpty(event.getMessage().getContent())
            .map(content -> Arrays.asList(content.split(" ")))
            .doOnNext(command -> playerManager.loadItem(command.get(1), scheduler))
            .then());


        
        // Gets Commands by looking for stuff that starts with . and splitting We will
        // be using . as our "prefix" to any command in the system.
        gordbot.getEventDispatcher().on(MessageCreateEvent.class)
                .flatMap(event -> Mono.justOrEmpty(event.getMessage().getContent())
                        .flatMap(content -> Flux.fromIterable(commands.entrySet())
                                .filter(entry -> content.startsWith(GordBot.COMMAND_PREFIX + entry.getKey()))
                                .flatMap(entry -> entry.getValue().execute(event)).next()))
                .subscribe();

        // try {
        // bootSkype();
        // }catch (InvalidCredentialsException | ConnectionException |
        // NotParticipatingException ignored){}
        gordbot.onDisconnect().block();

    }// end of MAIN
    
    
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




