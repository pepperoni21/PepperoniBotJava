package fr.pepperoni21.pepperonibot;

import fr.pepperoni21.pepperonibot.core.PepperoniBot;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.security.auth.login.LoginException;

public class Main extends ListenerAdapter {

    public static void main(String[] args) {
        try {
            JDA jda = Connector.connect();
            new PepperoniBot(jda);
        } catch (LoginException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}