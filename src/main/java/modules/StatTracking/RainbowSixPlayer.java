package modules.StatTracking;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import modules.StatTracking.RainbowSixDeclarations.Operator;
import modules.StatTracking.RainbowSixDeclarations.Platform;
import modules.StatTracking.RainbowSixDeclarations.Region;
import org.apache.commons.lang3.StringUtils;
import utils.http.ApiRequest;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.StringJoiner;

public class RainbowSixPlayer {

    private Auth authorizer;

    private String profileId;
    private String userId;
    private String idOnPlatform;
    private String nameOnPlatform;
    private Platform platform;

    private int level;
    private int ranked_xp;
    private int lootbox_probability;

    private int abandons;
    private int ranked_wins;
    private int ranked_losses;
    private int rank;
    private int max_rank;
    private double skill;
    private double skill_stdev;

    private HashMap<String, Operator> operators;
    private Operator[] sortedOperators;

    private int kills;
    private int deaths;
    private int wins;
    private int losses;
    private int penetration_kills;
    private int bullets_hit;
    private int melee_kills;
    private int bullets_fired;
    private int matches_played;
    private int assists;
    private int time_played;
    private int revives;
    private int headshots;
    private int dbno_assists;
    private int suicides;
    private int barricades_deployed;
    private int reinforcements_deployed;
    private int xp;
    private int rappel_breaches;
    private int distance_travelled;
    private int revives_denied;
    private int dbnos;
    private int gadgets_destroyed;
    private int blind_kills;

    private RainbowSixPlayer(Auth authorizer, String profileId, String userId, String idOnPlatform, String nameOnPlatform, Platform platform){
        this.authorizer = authorizer;
        this.profileId = profileId;
        this.userId = userId;
        this.idOnPlatform = idOnPlatform;
        this.nameOnPlatform = nameOnPlatform;
        this.platform = platform;
    }


    public void loadLevel(){
        JsonNode response = authorizer.authorizedRequest("https://public-ubiservices.ubi.com/v1/spaces/" + platform.getSpaceId() + "/sandboxes/"
                + platform.getUrl() + "/r6playerprofile/playerprofile/progressions")
                .setParameter("profile_ids", profileId)
                .get();
        JsonNode player = response.get("content").get("player_profiles").get(0);
        this.level = player.get("level").asInt();
        this.xp = player.get("xp").asInt();
        this.lootbox_probability = player.get("lootbox_probability").asInt();
    }

    public void loadRank(Region region, int season) {
        JsonNode response = authorizer.authorizedRequest("https://public-ubiservices.ubi.com/v1/spaces/" + platform.getSpaceId() + "/sandboxes/"
                + platform.getUrl() + "/r6karma/players")
                .setParameter("board_id", "pvp_ranked")
                .setParameter("profile_ids", profileId)
                .setParameter("region_id", region.getName())
                .setParameter("season_id", season + "")
                .get();
        JsonNode player = response.get("content").get("players").get(profileId);
        this.abandons = player.get("abandons").asInt();
        this.ranked_wins = player.get("wins").asInt();
        this.ranked_losses = player.get("losses").asInt();
        this.rank = player.get("rank").asInt();
        this.max_rank = player.get("max_rank").asInt();
        this.skill = player.get("skill_mean").asDouble();
        this.skill_stdev = player.get("skill_stdev").asDouble();

    }

    public void loadGeneral() {
        JsonNode stats = fetchStatistics("generalpvp_timeplayed", "generalpvp_matchplayed", "generalpvp_matchwon",
                "generalpvp_matchlost", "generalpvp_kills", "generalpvp_death",
                "generalpvp_bullethit", "generalpvp_bulletfired", "generalpvp_killassists",
                "generalpvp_revive", "generalpvp_headshot", "generalpvp_penetrationkills",
                "generalpvp_meleekills", "generalpvp_dbnoassists", "generalpvp_suicide",
                "generalpvp_barricadedeployed", "generalpvp_reinforcementdeploy", "generalpvp_totalxp",
                "generalpvp_rappelbreach", "generalpvp_distancetravelled", "generalpvp_revivedenied",
                "generalpvp_dbno", "generalpvp_gadgetdestroy", "generalpvp_blindkills");
        String stat = "generalpvp_";
        System.out.println(stats.toString());
        this.deaths = stats.get(stat + "death" + ":infinite").asInt();
        this.kills = stats.get(stat + "kills" + ":infinite").asInt();
        this.wins = stats.get(stat + "matchwon" + ":infinite").asInt();
        this.losses = stats.get(stat + "matchlost" + ":infinite").asInt();
        this.penetration_kills = stats.get(stat + "penetrationkills" + ":infinite").asInt();
        this.melee_kills = stats.get(stat + "meleekills" + ":infinite").asInt();
        //this.bullets_fired = stats.get(stat + "bulletfired" + ":infinite").asInt();
        this.bullets_hit = stats.get(stat + "bullethit" + ":infinite").asInt();
        this.matches_played = stats.get(stat + "matchplayed" + ":infinite").asInt();
        this.assists = stats.get(stat + "killassists" + ":infinite").asInt();
        this.time_played = stats.get(stat + "timeplayed" + ":infinite").asInt();
        this.revives = stats.get(stat + "revive" + ":infinite").asInt();
        this.headshots = stats.get(stat + "headshot" + ":infinite").asInt();
        this.dbno_assists = stats.get(stat + "dbnoassists" + ":infinite").asInt();
        this.suicides = stats.get(stat + "suicide" + ":infinite").asInt();
        this.barricades_deployed = stats.get(stat + "barricadedeployed" + ":infinite").asInt();
        this.reinforcements_deployed = stats.get(stat + "reinforcementdeploy" + ":infinite").asInt();
        this.xp = stats.get(stat + "totalxp" + ":infinite").asInt();
        this.rappel_breaches = stats.get(stat + "rappelbreach" + ":infinite").asInt();
        this.distance_travelled = stats.get(stat + "distancetravelled" + ":infinite").asInt();
        //this.revives_denied = stats.get(stat + "revivedenied" + ":infinite").asInt();
        this.dbnos = stats.get(stat + "dbno" + ":infinite").asInt();
        this.gadgets_destroyed = stats.get(stat + "gadgetdestroy" + ":infinite").asInt();
        this.blind_kills = stats.get(stat + "blindkills" + ":infinite").asInt();
    }

    public void loadOperators(){
        String statistics = "operatorpvp_kills,operatorpvp_death,operatorpvp_roundwon,operatorpvp_roundlost,operatorpvp_meleekills,operatorpvp_totalxp,operatorpvp_headshot,operatorpvp_timeplayed,operatorpvp_dbno";
        StringJoiner operatorsStringJoiner = new StringJoiner(",");

        JsonNode defs = getOperatorDefs();
        for(JsonNode operatorDef : defs){
            operatorsStringJoiner.add(operatorDef.get("uniqueStatistic").get("pvp").get("statisticId").asText().split(":")[0]);
        }
        statistics += "," + operatorsStringJoiner;

        JsonNode response = fetchStatistics(statistics);
        operators = new HashMap<>();
        for(JsonNode operator : defs){
            if(response.get("operatorpvp_timeplayed:" + operator.get("index").asText() + ":infinite") != null) { //If the player has time on the operator
                operators.put(operator.get("id").asText(), new Operator(StringUtils.capitalize(operator.get("id").asText()), getPvpNode(response, operator, "kills").asInt(),
                        getPvpNode(response, operator, "kills").asInt(), getPvpNode(response, operator, "roundwon").asInt(),
                        getPvpNode(response, operator, "roundlost").asInt(), getPvpNode(response, operator, "headshot").asInt(),
                        getPvpNode(response, operator, "meleekills").asInt(), getPvpNode(response, operator, "dbno").asInt(),
                        getPvpNode(response, operator, "totalxp").asInt(), getPvpNode(response, operator, "timeplayed").asLong(),
                        operator.get("uniqueStatistic").get("pvp").get("statisticId").asText(), getPvpNode(response, operator,
                        operator.get("uniqueStatistic").get("pvp").get("statisticId").asText() + ":infinite").asInt(), operator.get("category").asText()));
            }
        }
    }

    private JsonNode getPvpNode(JsonNode responseContent, JsonNode operator, String stat){
        JsonNode node = responseContent.get("operatorpvp_"+ stat + ":" + operator.get("index").asText() + ":" + "infinite");
        if(node == null) {
            try {
                return new ObjectMapper().readTree("0");
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            return node;
        }
    }

    public void loadGamemodes(){
        JsonNode stats = fetchStatistics("secureareapvp_matchwon", "secureareapvp_matchlost", "secureareapvp_matchplayed",
                "secureareapvp_bestscore", "rescuehostagepvp_matchwon", "rescuehostagepvp_matchlost",
                "rescuehostagepvp_matchplayed", "rescuehostagepvp_bestscore", "plantbombpvp_matchwon",
                "plantbombpvp_matchlost", "plantbombpvp_matchplayed", "plantbombpvp_bestscore",
                "generalpvp_servershacked", "generalpvp_serverdefender", "generalpvp_serveraggression",
                "generalpvp_hostagerescue", "generalpvp_hostagedefense");
        System.out.println(stats.toString());
    }

    private JsonNode fetchStatistics(String... statistics){
        StringJoiner concat = new StringJoiner(",");
        for(String stat : statistics){
            concat.add(stat);
        }
        return authorizer.authorizedRequest("https://public-ubiservices.ubi.com/v1/spaces/" + platform.getSpaceId() + "/sandboxes/"
                + platform.getUrl() + "/playerstats2/statistics")
                .setParameter("populations", profileId)
                .setParameter("statistics", concat.toString())
                .get().get("content").get("results").get(profileId);
    }
    public HashMap<String, Operator> getOperators() {
        if(operators == null)
            loadOperators();
        return operators;
    }

    private JsonNode getOperatorDefs() {
        if(Operator.OPERATOR_DEFS == null)
            Operator.OPERATOR_DEFS = new ApiRequest("https://ubistatic-a.akamaihd.net/0058/prod/assets/data/operators.3a2655c8.json")
                    .get().get("content");
        return Operator.OPERATOR_DEFS;
    }

    public Operator[] getSortedOperators() {
        if(sortedOperators == null){
            Operator[] sortedOps = getOperators().values().toArray(new Operator[0]);
            Arrays.sort(sortedOps);
            sortedOperators = sortedOps;
        }
        return sortedOperators;
    }

    public Operator getTopAttackOperator() {
        for(Operator operator : getSortedOperators()) {
            if(operator.getSide().equalsIgnoreCase("atk")){
                return operator;
            }
        }
        return null;
    }
    public Operator getTopDefenseOperator() {
        for(Operator operator : getSortedOperators()) {
            if(operator.getSide().equalsIgnoreCase("def")){
                return operator;
            }
        }
        return null;
    }

    public double getRankedWinLossRatio() {
        return (ranked_wins*1.0) / (ranked_losses*1.0);
    }

    public String getProfileId() {
        return profileId;
    }

    public String getNameOnPlatform() {
        return nameOnPlatform;
    }

    public Platform getPlatform() {
        return platform;
    }

    public int getLevel() {
        return level;
    }

    public int getRankedXp() {
        return ranked_xp;
    }

    public int getLootboxProbability() {
        return lootbox_probability;
    }

    public int getAbandons() {
        return abandons;
    }

    public int getRankedWins() {
        return ranked_wins;
    }

    public int getRankedLosses() {
        return ranked_losses;
    }

    public int getRank() {
        return rank;
    }

    public int getMaxRank() {
        return max_rank;
    }

    public double getSkill() {
        return skill;
    }

    public double getSkillStandardDeviation() {
        return skill_stdev;
    }

    public int getKills() {
        return kills;
    }

    public int getDeaths() {
        return deaths;
    }

    public int getWins() {
        return wins;
    }

    public int getLosses() {
        return losses;
    }

    public int getPenetrationKills() {
        return penetration_kills;
    }

    public int getBulletsHit() {
        return bullets_hit;
    }

    public int getMeleeKills() {
        return melee_kills;
    }

    public int getBulletsFired() {
        return bullets_fired;
    }

    public int getMatchesPlayed() {
        return matches_played;
    }

    public int getAssists() {
        return assists;
    }

    public int getTimePlayed() {
        return time_played;
    }

    public int getRevives() {
        return revives;
    }

    public int getHeadshots() {
        return headshots;
    }

    public int getDbnoAssists() {
        return dbno_assists;
    }

    public int getSuicides() {
        return suicides;
    }

    public int getBarricadesDeployed() {
        return barricades_deployed;
    }

    public int getReinforcementsDeployed() {
        return reinforcements_deployed;
    }

    public int getXp() {
        return xp;
    }

    public int getRappelBreaches() {
        return rappel_breaches;
    }

    public int getDistanceTravelled() {
        return distance_travelled;
    }

    public int getRevivesDenied() {
        return revives_denied;
    }

    public int getDbnos() {
        return dbnos;
    }

    public int getGadgetsDestroyed() {
        return gadgets_destroyed;
    }

    public int getBlindKills() {
        return blind_kills;
    }

    public static RainbowSixPlayer getPlayer(Auth auth, String nameOnPlatform, Platform platform){
        JsonNode response = auth.authorizedRequest("https://connect.ubi.com/ubiservices/v2/profiles")
                .setParameter("nameOnPlatform", nameOnPlatform)
                .setParameter("platformType", platform.getName())
                .get();
        JsonNode player = response.get("content").get("profiles").get(0);
        return new RainbowSixPlayer(auth, player.get("profileId").asText(), player.get("userId").asText(),
                player.get("idOnPlatform").asText(), player.get("nameOnPlatform").asText(), Platform.getByName(player.get("platformType").asText()));
    }
}
