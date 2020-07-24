package arbitrationBotTaskForVectree;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class SearchArbitrationSituation {

    private static ConcurrentHashMap<String, OrderBookJava> orderBooksJava;

    public SearchArbitrationSituation(){};
    public SearchArbitrationSituation(ConcurrentHashMap<String,OrderBookJava> orderBooksJava){
        this.orderBooksJava = orderBooksJava;
    }

    public void startParsing(){
        //здесь определяем ексекутор сервис, фор для массива и внутри него канкарент,
        //что бы каждый парсинг шел на новом потоке

    }

    public static void setOrderBooksJava(ConcurrentHashMap<String, OrderBookJava> orderBooksJava) {
        SearchArbitrationSituation.orderBooksJava = orderBooksJava;
    }

    //метод который парсит данную ситуацию
    private List parsingThisSiuation(OrderBookJava orderBookJava) {

    return null; }

    //метод который из пары валют делит на валюты, для их последующих сравнений
    private String[] returnOfCurrencyFromAPair(String pairCurrency) {
        return null;
    }
}
