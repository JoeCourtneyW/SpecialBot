package modules.Steam;


import com.fasterxml.jackson.databind.JsonNode;

public class SteamGame {
    public String APPID;
    public String NAME;
    public boolean FREE;
    public double INITIAL_PRICE;
    public double FINAL_PRICE;
    public int DISCOUNT_PERCENT;
    public String HEADER_IMAGE;
    public String DEVELOPER;
    public String DESCRIPTION;
    public boolean RELEASED;
    public String RELEASE_DATE; // DD mmm, YYYY

    public String steamLink(){
        return "http://store.steampowered.com/app/" + APPID + "/";
    }

    public static SteamGame buildFromAppDetails(JsonNode appData){
        if(appData.get("data") != null)
            appData = appData.get("data");
        SteamGame game = new SteamGame();
        game.APPID = appData.get("steam_appid").asText();
        game.NAME = appData.get("name").asText();
        if((game.FREE = appData.get("is_free").asBoolean())){
            game.INITIAL_PRICE = appData.get("price_overview").get("initial").asDouble() / 100;
            game.FINAL_PRICE = appData.get("price_overview").get("final").asDouble() / 100;
            game.DISCOUNT_PERCENT = appData.get("price_overview").get("discount_percent").asInt();
        } else {
            game.INITIAL_PRICE = 0;
            game.FINAL_PRICE = 0;
            game.DISCOUNT_PERCENT = 0;
        }
        game.HEADER_IMAGE = appData.get("header_image").asText();
        game.DEVELOPER = appData.get("developers").get(0).asText();
        game.DESCRIPTION = appData.get("short_description").asText();
        game.RELEASED = appData.get("release_date").get("coming_soon").asBoolean();
        game.RELEASE_DATE = appData.get("release_date").get("date").asText();
        return game;
    }
}
