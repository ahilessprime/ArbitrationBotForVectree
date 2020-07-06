package arbitrationBotTaskForVectree;



import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class Main {

    private static ArrayList<URL> urlList;
    private static HashMap<String, JsonObject> stakanTorgov;

    public static void main(String[] args) {

        //создаем коллекцию ссылок для парсинга биржи
        UrlBulder urlBulder = new UrlBulder(new Currencies(),30);
        urlList = urlBulder.getUrlList();

        //подключаемся к нету, парсим ссылки и получаем по ним объект
        ConnectAndParsing connectAndParsing = new ConnectAndParsing(urlList);
        //получаем результат парсинга
        stakanTorgov = connectAndParsing.getResultParsing();
        System.out.println(stakanTorgov.size());
        System.out.println(stakanTorgov.get("BTC_ETH"));
        //а теперь как работать с этим жейсоном
        //ну ок, с этим как то разобрался. обновил список валют
        //там их дофига а запускать всех их в новых потоках тупо.
        //нужно разобраться с executorom и отрефакторить connectAndParsing под него
        


    }
}

class UrlBulder{

    //следующие переменные, это покромсанная ссылка на биржу. для последующего его парсинга
    //первая часть ссылки
    private final String EXCHANGE_URL = "https://poloniex.com/public?" +
            "command=returnOrderBook&currencyPair=";
    private ArrayList<String> currencies; //список валют
    // нужно приставить в конец, значение глубины запроса, от 1 до 100
    private String DEPTH_FOR_STRING = "&depth=";


    //конструктор принимает только список валют
    public UrlBulder(ArrayList<String> currencies){
        this.currencies = currencies;
        DEPTH_FOR_STRING += 10;
    }
    //конструктор принимает список валют и глубину запроса
    public UrlBulder(ArrayList<String> currencies, int depthValue){
        this.currencies = currencies;
        DEPTH_FOR_STRING += depthValue;
    }


    //возвращает коллекцию возможных ссылок
    public ArrayList<URL> getUrlList(){
        //создаем объект коллекции ссылок
        ArrayList<URL> UrlList = new ArrayList<>();
        //парсим коллекцию с валютами
        for(String currency : currencies){
            //создаем ссылки на каждую валюту
            UrlList.add( createUrl(EXCHANGE_URL + currency + DEPTH_FOR_STRING) );
        }
        //возвращаем готовую коллекцию ссылок
        return UrlList;
    }


    // создаем объект URL из указанной в параметре строки
    public static URL createUrl(String link) {
        try {
            return new URL(link);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }


}
