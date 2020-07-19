package arbitrationBotTaskForVectree;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;


import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class JsonToJavaObject {

    private  HashMap<String, JsonObject> orderBookJson;
    private ConcurrentHashMap<String, OrderBookJava> orderBooksJava
            = new ConcurrentHashMap<>();

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


            HashMap<Float, Float> formattingAsksToOrderBookJava = new HashMap<>();

            for (String keyFloat: primitiveObject.getAsks().keySet()){
                Float value = Float.parseFloat(keyFloat);
                formattingAsksToOrderBookJava.put(value, primitiveObject.getAsks().get(keyFloat));
            }


            HashMap<Float, Float> formattingBidsToOrderBookJava = new HashMap<>();

            for (String keyFloat: primitiveObject.getBids().keySet()){
                Float value = Float.parseFloat(keyFloat);
                formattingBidsToOrderBookJava.put(value, primitiveObject.getBids().get(keyFloat));
            }

            OrderBookJava orderBJ = new OrderBookJava(key, formattingAsksToOrderBookJava,
                    formattingBidsToOrderBookJava);

            orderBooksJava.put(key, orderBJ);
        }

    }


    public void setOrderBookJson(HashMap<String, JsonObject> orderBookJson) {
        this.orderBookJson = orderBookJson;
        changeJsonToJava();
    }

    public ConcurrentHashMap<String, OrderBookJava> getOrderBooksJava(){
        return orderBooksJava;
    }
}

class PrimitiveObject{

    public PrimitiveObject(){}

    //@SerializedName("asks")
    private Map <String,Float> asks = new HashMap<>();
    //@SerializedName("bids")
    private Map <String,Float> bids = new HashMap<>();


    public Map<String, Float> getAsks() {
        return asks;
    }

    public void setAsks(Map<String, Float> asks) {
        this.asks = asks;
    }

    public Map<String, Float> getBids() {
        return bids;
    }

    public void setBids(Map<String, Float> bids) {
        this.bids = bids;
    }
}





