package arbitrationBotTaskForVectree;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;


import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;



/**
 * класс JsonToJavaObject, как следуеот от имени, предназначен для десериализации объектов
 * Json к Java.
 *
 * конструктор в качестве аргумента принимает коллекцию HashMap из Json объектов и запускает
 * парсинг, которая приводит колекцию JsonObject-ов к колекции JavaObject
 *
 * метод setOrderBookJson  в качестве аргумента принимает коллекцию HashMap из Json объектов и запускает
 *  * парсинг, которая приводит колекцию JsonObject-ов к колекции JavaObject
 *
 * метод getOrderBooksJava возвращает уже готовую коллекцию ConcurrentHashMap<String, OrderBookJava>
 * приведенная к JavaObject-ам.
 */



public class JsonToJavaObject {

    //полученная коллекция Json Объектов
    private  HashMap<String, JsonObject> orderBookJson;
    //коллекция, которая должна получится после приведения Json
    private ConcurrentHashMap<String, OrderBookJava> orderBooksJava
            = new ConcurrentHashMap<>();


    //конструктора:
    public JsonToJavaObject(){}
    public JsonToJavaObject(HashMap<String, JsonObject> orderBookJson){
        this.orderBookJson = orderBookJson; //получаем Json
        changeJsonToJava(); //начинаем его переобразовывать
    }


    //метод приводящий Json к Java
    private void changeJsonToJava(){
        for (String key: orderBookJson.keySet()){

            //готовим Json Объект
            JsonObject jsonObject = orderBookJson.get(key);
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            //приводим Json к JavaObject
            PrimitiveObject primitiveObject =  gson.fromJson(jsonObject, PrimitiveObject.class);

            //коллекция полностью готовых значений Asks
            HashMap<Float, Float> formattingAsksToOrderBookJava = new HashMap<>();
            //фильтруем примитивную коллекцию полученную из json
            for (String keyFloat: primitiveObject.getAsks().keySet()){
                Float value = Float.parseFloat(keyFloat); //приводим String ключи к формату Float
                formattingAsksToOrderBookJava.put(value, primitiveObject.getAsks().get(keyFloat));
            }


            //коллекция полностью готовых значений Bids
            HashMap<Float, Float> formattingBidsToOrderBookJava = new HashMap<>();
            //фильтруем примитивную коллекцию полученную из json, принцип тот же
            for (String keyFloat: primitiveObject.getBids().keySet()){
                Float value = Float.parseFloat(keyFloat);
                formattingBidsToOrderBookJava.put(value, primitiveObject.getBids().get(keyFloat));
            }

            //создаем полностью готовый JavaObject аналогичный JsonObject-у
            OrderBookJava orderBookJava = new OrderBookJava(key, formattingAsksToOrderBookJava,
                    formattingBidsToOrderBookJava);
            //и добавляем этот объект в коллекцию подобных
            orderBooksJava.put(key, orderBookJava);
        }

    }


    public void setOrderBookJson(HashMap<String, JsonObject> orderBookJson) {
        if (orderBookJson != null) return;
        this.orderBookJson = orderBookJson;
        changeJsonToJava();
    }

    public ConcurrentHashMap<String, OrderBookJava> getOrderBooksJava(){
        return orderBooksJava;
    }
}


/**
 * класс PrimitiveObject, храванит в себе данные приведенные из Json объекта,
 * однако, требующие что бы переменные были приведенны к требуемым примитивным типам.
 */

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





