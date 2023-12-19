package eu.proxyservices.bowbash.game.data;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bukkit.Bukkit;

import java.util.*;

import static com.mongodb.client.model.Filters.eq;
import static eu.proxyservices.bowbash.game.data.StatsType.BLOCKS_PLACED;

public class StatsManager {

    private static MongoClient mongoClient = null;

    private static MongoCollection<Document> coll = null;
    private static boolean isConnected;
    private static boolean isEnabled = true;

    static  {
        HashMap<String, String> settings = ConfigManager.loadDatabaseSettings();
        try {
            String url = "mongodb+srv://" + settings.get("username") + ":" + settings.get("password") + "@" + settings.get("url") + "/?retryWrites=true&w=majority";
            StatsManager.mongoClient = MongoClients.create(url);
            StatsManager.coll = mongoClient.getDatabase(settings.get("database")).getCollection(settings.get("collection"));
        } catch (Exception e) {
            mongoClient = null;
        }

        if (mongoClient != null) {
            isConnected = true;
            Bukkit.getConsoleSender().sendMessage("§7[§eStats§7] §aConnected to database!");
        } else {
            disableStats();
            Bukkit.getConsoleSender().sendMessage("§7[§eStats§7] §cCould not connect to database!");
            Bukkit.getConsoleSender().sendMessage("§7[§eStats§7] §cStats are disabled for this session!");
        }
    }

    public static void disconnect() {
        if (isConnected) {
            mongoClient.close();
        }
    }

    public static Document getDoc(UUID uuid) {
        return coll.find(eq("uuid", uuid.toString())).first();
    }

    public static void createUser(UUID uuid) {
        Document doc = new Document()
                .append("uuid", uuid.toString())
                .append("kills", 0)
                .append("deaths", 0)
                .append("shots", 0)
                .append("wins", 0)
                .append("blocks_placed", 0);
        coll.insertOne(doc);
    }

    public static boolean saveAllUser() {
        if (isConnected) {
            for (UUID uuid : loaded) {
                Document doc = getDoc(uuid);
                doc.append("kills", kills.get(uuid));
                doc.append("deaths", deaths.get(uuid));
                doc.append("shots", shots.get(uuid));
                doc.append("blocks_placed", blocks_placed.get(uuid));
                doc.append("wins", wins.get(uuid));
                coll.findOneAndReplace(eq("uuid", uuid.toString()), doc);
            }
            return true;
        } else {
            return false;
        }
    }

    static HashMap<UUID, Integer> kills = new HashMap<>();
    static HashMap<UUID, Integer> deaths = new HashMap<>();
    static HashMap<UUID, Integer> shots = new HashMap<>();
    static HashMap<UUID, Integer> blocks_placed = new HashMap<>();
    static HashMap<UUID, Integer> wins = new HashMap<>();
    static List<UUID> loaded = new ArrayList<>();

    private static void loadUser(UUID uuid) {
        if (!isConnected) {
            return;
        }
        Document doc = getDoc(uuid);
        if (doc == null) {
            createUser(uuid);
            kills.put(uuid, 0);
            deaths.put(uuid, 0);
            shots.put(uuid, 0);
            blocks_placed.put(uuid, 0);
            wins.put(uuid, 0);
            loaded.add(uuid);
        } else {
            kills.put(uuid, doc.getInteger("kills"));
            deaths.put(uuid, doc.getInteger("deaths"));
            shots.put(uuid, doc.getInteger("shots"));
            blocks_placed.put(uuid, doc.getInteger("blocks_placed"));
            wins.put(uuid, doc.getInteger("wins"));
            loaded.add(uuid);
        }
    }

    public static Map<StatsType, Integer> getStats(UUID uuid) {
        HashMap<StatsType, Integer> stats = new HashMap<>();
        if (!loaded.contains(uuid)) {
            loadUser(uuid);
        }
        if (!isEnabled) {
            return null;
        }
        for (StatsType statsType : StatsType.values()) {
            switch (statsType) {
                case KILLS: {
                    stats.put(StatsType.KILLS, kills.get(uuid));
                    break;
                }
                case DEATHS: {
                    stats.put(StatsType.DEATHS, deaths.get(uuid));
                    break;
                }
                case SHOTS: {
                    stats.put(StatsType.SHOTS, shots.get(uuid));
                    break;
                }
                case BLOCKS_PLACED: {
                    stats.put(BLOCKS_PLACED, blocks_placed.get(uuid));
                    break;
                }
                case WINS: {
                    stats.put(StatsType.WINS, wins.get(uuid));
                    break;
                }
            }
        }
        return stats;
    }
    public static void updateStat(UUID uuid, StatsType st) {
        if (!isEnabled) {
            return;
        }
        if (!loaded.contains(uuid)) {
            loadUser(uuid);
        }
        switch (st) {
            case KILLS: {
                kills.put(uuid, kills.get(uuid) + 1);
                break;
            }
            case DEATHS: {
                deaths.put(uuid, deaths.get(uuid) + 1);
                break;
            }
            case SHOTS: {
                shots.put(uuid, shots.get(uuid) + 1);
                break;
            }
            case BLOCKS_PLACED: {
                blocks_placed.put(uuid, blocks_placed.get(uuid) + 1);
                break;
            }
            case WINS: {
                wins.put(uuid, wins.get(uuid) + 1);
                break;
            }
        }
    }

    public static boolean isEnabled() {
        return isEnabled;
    }

    public static void disableStats() {
        isEnabled = false;
    }

    public static boolean enableStats() {
        if (isConnected) {
            isEnabled = true;
            return true;
        } else {
            return false;
        }
    }

}
