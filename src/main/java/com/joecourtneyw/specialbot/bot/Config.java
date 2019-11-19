package com.joecourtneyw.specialbot.bot;

import java.time.ZoneId;
import java.util.Properties;

public class Config {


    public static final String GIT_URL = "https://github.com/JoeCourtneyW/SpecialBot/tree/redux";
    public static final String DONATION_URL = "";
    public static final String DONATION_BTC = "";
    public static final String DONATION_ETH = "";
    public static final String STATUS_MESSAGE = ".help";
    public static final long SLY_ID = 107131529318117376L;
    public static final String SLY_MENTION = "@Hazeluff#0201";
    public static final String SLY_SITE = "http://www.hazeluff.com";
    public static final String SLY_EMAIL = "eugene@hazeluff.com";
    public static final String VERSION = "${project.version}";
    //public static final String MONGO_HOST = "";
    //public static final int MONGO_PORT = ;
    //public static final String MONGO_DATABASE_NAME = "";
    //public static final String MONGO_TEST_DATABASE_NAME = "";
    public static final ZoneId DATE_START_TIME_ZONE = ZoneId.of("EST");

    public static final int HTTP_REQUEST_RETRIES = 5;

    private static final Properties systemProperties = System.getProperties();
}
