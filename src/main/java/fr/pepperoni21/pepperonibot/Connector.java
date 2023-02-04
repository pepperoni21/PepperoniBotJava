package fr.pepperoni21.pepperonibot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import javax.security.auth.login.LoginException;

import static fr.pepperoni21.pepperonibot.References.DOTENV;
import static fr.pepperoni21.pepperonibot.core.PepperoniBot.LOGGER;

public class Connector {

    public static JDA connect() throws LoginException, InterruptedException {
        LOGGER.info("Connecting to Discord...");

        String token = DOTENV.get("DISCORD_BOT_TOKEN");
        JDABuilder builder = JDABuilder.createDefault(token);

        builder.setBulkDeleteSplittingEnabled(false);
        builder.setStatus(OnlineStatus.ONLINE);
        builder.disableCache(CacheFlag.MEMBER_OVERRIDES, CacheFlag.VOICE_STATE);
        builder.enableIntents(GatewayIntent.MESSAGE_CONTENT);

        LOGGER.info("Connected to Discord!");

        return builder.build().awaitReady();
    }

}
