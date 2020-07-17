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
            PrimitiveObject primitiveObject =  gson.fromJson(jsonObject, PrimitiveObject.class);
            System.out.println(primitiveObject.getAsks());

        }
    }

    public void setOrderBookJson(HashMap<String, JsonObject> orderBookJson) {
        this.orderBookJson = orderBookJson;
        changeJsonToJava();
    }
}

class PrimitiveObject{

    public PrimitiveObject(){}

    //@SerializedName("asks")
    private Map <String,Double> asks = new HashMap<>();
    //@SerializedName("bids")
    private Map <String,Double> bids = new HashMap<>();

    public Map<String, Double> getAsks() {
        return asks;
    }

    public void setAsks(Map<String, Double> asks) {
        this.asks = asks;
    }

    public Map<String, Double> getBids() {
        return bids;
    }

    public void setBids(Map<String, Double> bids) {
        this.bids = bids;
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
