package com.joecourtneyw.specialbot.bot;

public class SpecialBotException extends RuntimeException{
    public SpecialBotException(RuntimeException e) {
        super(e);
    }

    public SpecialBotException(String string) {
        super(string);
    }

    private static final long serialVersionUID = -429202487421777493L;
}
