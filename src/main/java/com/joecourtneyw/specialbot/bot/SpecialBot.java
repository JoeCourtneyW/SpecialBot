package com.joecourtneyw.specialbot.bot;

import com.joecourtneyw.specialbot.bot.discord.DiscordManager;
import com.joecourtneyw.specialbot.utils.Utils;
import discord4j.core.DiscordClient;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.object.entity.TextChannel;
import discord4j.core.object.presence.Activity;
import discord4j.core.object.presence.Presence;
import discord4j.core.spec.MessageCreateSpec;
import discord4j.rest.http.client.ClientException;
import discord4j.rest.request.RouteMatcher;
import discord4j.rest.request.RouterOptions;
import discord4j.rest.response.ResponseFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.retry.Retry;
import reactor.util.function.Tuple2;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class SpecialBot extends Thread {
        private static final Logger LOGGER = LoggerFactory.getLogger(SpecialBot.class);

        private static final Presence STARTING_UP_PRESENCE = Presence.doNotDisturb(Activity.watching("itself start up"));
        private static final Presence ONLINE_PRESENCE = Presence.online(Activity.playing(Config.STATUS_MESSAGE));


        private AtomicReference<DiscordManager> discordManager = new AtomicReference<>();

        private SpecialBot() {
            this.discordManager = null;
        }

        SpecialBot(DiscordManager discordManager) {
            this.discordManager.set(discordManager);
        }

        public static SpecialBot create(String botToken) {
            LOGGER.info("Running SpecialBot v" + Config.VERSION);
            Thread.currentThread().setName("SpecialBot");

            SpecialBot specialBot = new SpecialBot();

            // Init MongoClient/GuildPreferences
            specialBot.initPreferences();

            // Init Discord Client
            specialBot.initDiscord(botToken);

            while (specialBot.getDiscordManager() == null) {
                LOGGER.info("Waiting for Discord client to be ready.");
                Utils.sleep(5000);
            }

            LOGGER.info("Attaching Listener.");
            /*MessageListener messageListener = new MessageListener(nhlBot);
            nhlBot.getDiscordManager().getClient().getEventDispatcher()
                    .on(MessageCreateEvent.class)
                    .map(event -> messageListener.getReply(event))
                    .doOnError(t -> LOGGER.error("Error occurred when responding to message.", t))
                    .retry()
                    .subscribe(NHLBot::sendMessage);*/

            specialBot.getDiscordManager().changePresence(STARTING_UP_PRESENCE);
            LOGGER.info("SpecialBot Started. id [" + specialBot.getDiscordManager().getId() + "]");


            specialBot.getDiscordManager().changePresence(ONLINE_PRESENCE);

            specialBot.start();

            return specialBot;
        }

        private static void sendMessage(Mono<Tuple2<Consumer<MessageCreateSpec>, TextChannel>> replyMono) {
            Tuple2<Consumer<MessageCreateSpec>, TextChannel> reply = replyMono.block();
            if (reply != null) {
                reply.getT2().createMessage(reply.getT1()).subscribe();
            }
        }

        /**
         * This needs to be done in its own Thread. login().block() hold the execution.
         *
         * @param botToken
         */
        public void initDiscord(String botToken) {
            new Thread(() -> {
                LOGGER.info("Initializing Discord.");
                // Init DiscordClient and DiscordManager
                DiscordClient discordClient = new DiscordClientBuilder(botToken)
                        .setRouterOptions(RouterOptions.builder()
                                // globally suppress any not found (404) error
                                .onClientResponse(ResponseFunction.emptyIfNotFound())
                                // 403 Forbidden will not be retried.
                                .onClientResponse(ResponseFunction
                                        .emptyOnErrorStatus(RouteMatcher.any(), 403))
                                .onClientResponse(ResponseFunction.retryWhen(RouteMatcher.any(),
                                        Retry.onlyIf(ClientException.isRetryContextStatusCode(500))
                                                .exponentialBackoffWithJitter(
                                                        Duration.ofSeconds(2),
                                                        Duration.ofSeconds(10))))
                                .build())
                        .build();

                discordClient.getEventDispatcher().on(ReadyEvent.class)
                        .subscribe(event -> {
                            discordManager.set(new DiscordManager(discordClient));
                            LOGGER.info("Discord Client is ready.");
                        });

                // Login
                LOGGER.info("Logging into Discord.");
                discordClient.login().block();
            }).start();
            LOGGER.info("Discord Initializer started.");
        }

        @Override
        public void run() {
            while (!isInterrupted()) {
                getDiscordManager().changePresence(ONLINE_PRESENCE);
                Utils.sleep(3600000L);
            }
        }

        void initPreferences() {
            LOGGER.info("Initializing Preferences.");
            //this.preferencesManager = PreferencesManager.getInstance();
        }


        public DiscordManager getDiscordManager() {
            return discordManager.get();
        }

        /**
         * Gets the mention for the bot. It is how the raw message displays a mention of
         * the bot's user.
         *
         * @return
         */
        public String getMention() {
            return "<@" + getDiscordManager().getId().asString() + ">";
        }

        /**
         * Gets the id of the bot, in the format displayed in a message, when the bot is
         * mentioned by Nickname.
         *
         * @return
         */
        public String getNicknameMentionId() {
            return "<@!" + getDiscordManager().getId().asString() + ">";
        }
}
