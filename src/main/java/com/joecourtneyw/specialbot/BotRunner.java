package com.joecourtneyw.specialbot;

import com.joecourtneyw.specialbot.bot.ExceptionHandler;
import com.joecourtneyw.specialbot.bot.SpecialBot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BotRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(BotRunner.class);

    @SuppressWarnings("unused")
    public static void main(String[] args) {
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(BotRunner.class));

        SpecialBot bot = SpecialBot.create(args[0]);
    }
}
