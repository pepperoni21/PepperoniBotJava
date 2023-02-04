package fr.pepperoni21.pepperonibot.core;

import fr.pepperoni21.pepperonibot.core.db.DBManager;
import fr.pepperoni21.pepperonibot.core.order.OrderManager;
import fr.pepperoni21.pepperonibot.core.review.ReviewManager;
import net.dv8tion.jda.api.JDA;

import java.util.logging.Logger;

public class PepperoniBot {

    public static final Logger LOGGER = Logger.getLogger("PepperoniBot");

    private static PepperoniBot instance;

    private final JDA jda;

    public PepperoniBot(JDA jda) {
        instance = this;
        this.jda = jda;

        new DBManager();

        new OrderManager();
        new ReviewManager();
    }

    public JDA getJDA() {
        return jda;
    }

    public static PepperoniBot getInstance() {
        return instance;
    }
}
