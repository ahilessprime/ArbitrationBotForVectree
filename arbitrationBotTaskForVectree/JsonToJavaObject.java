package arbitrationBotTaskForVectree;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class JsonToJavaObject {

    private  HashMap<String, JsonObject> orderBookJson;
    private ConcurrentHashMap<String, OrderBookJava> orderBooksJava;

    public JsonToJavaObject(HashMap<String, JsonObject> orderBookJson){
        this.orderBookJson = orderBookJson;
        changeJsonToJava();
    }

    private void changeJsonToJava(){
        for (String key: orderBookJson.keySet()){
            JsonObject jsonObject = orderBookJson.get(key);
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            class PrimitiveObject{
                //public HashMap<String, Double> asks;
                //public HashMap<String, Double> bids;
                public Map<String, Double> asks;
                public Map<String, Double> bids;
                public String isFrozen;
                public int seq;
            }

            PrimitiveObject primitiveObject = gson.fromJson(jsonObject, PrimitiveObject.class);
            System.out.println(primitiveObject.asks.get(0));

        }
    }

    public void setOrderBookJson(HashMap<String, JsonObject> orderBookJson) {
        this.orderBookJson = orderBookJson;
        changeJsonToJava();
    }
}




class OrderBookJava {

    private String nameСurrency;
    private HashMap<Double, Double> asks = new HashMap<>();
    private HashMap<Double, Double> bids = new HashMap<>();

    public OrderBookJava(String nameСurrency, HashMap asks, HashMap bids ){
        this.nameСurrency = nameСurrency;
        this.asks = asks;
        this.bids = bids;
    }

    public String getNameСurrency() {
        return nameСurrency;
    }

    public HashMap<Double, Double> getAsks() {
        return asks;
    }

    public HashMap<Double, Double> getBids() {
        return bids;
    }
}
