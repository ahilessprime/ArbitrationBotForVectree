package arbitrationBotTaskForVectree;

import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class SearchArbitrationSituation {

    private static ConcurrentHashMap<String, OrderBookJava> orderBooksJava;

    //менеджер потоков на 20 потоков
    private ExecutorService executorService = Executors.newFixedThreadPool(20);

    private HashMap<String, FutureTask<List>> resultParsing = new HashMap<>();

    public SearchArbitrationSituation(){};
    public SearchArbitrationSituation(ConcurrentHashMap<String,OrderBookJava> orderBooksJava){
        this.orderBooksJava = orderBooksJava;
    }

    public void startParsing(){
        //здесь определяем ексекутор сервис, фор для массива и внутри него канкарент,
        //что бы каждый парсинг шел на новом потоке
        if(orderBooksJava == null) { //если не пользовались конструктором или сеттером
            throw new NullPointerException("The Order Book is not initialized in class");}

        for (String currencyPair : orderBooksJava.keySet()){

            //взываем нужную нам хрень
            OrderBookJava orderBook = orderBooksJava.get(currencyPair);

            Callable<List> callable = () -> {

                return parsingThisSiuation(orderBook, orderBooksJava);
            };


            FutureTask<List> future;

            //поток получающий объект, ставим в executorServis в очередь,
            //результат выполнения которого получит future
            future = (FutureTask<List>) executorService.submit(callable);

            resultParsing.put(currencyPair, future);
        }

    }


    //метод который парсит данную ситуацию
    private List parsingThisSiuation(OrderBookJava orderBookJava, Map orderBooksJava) {

        String nameСurrency = orderBookJava.getNameСurrency();

    return null; }

    //метод который из пары валют делит на валюты, для их последующих сравнений
    private String[] returnOfCurrencyFromAPair(String pairCurrency) {
        return null;
    }

    public static void setOrderBooksJava(ConcurrentHashMap<String, OrderBookJava> orderBooksJava) {
        SearchArbitrationSituation.orderBooksJava = orderBooksJava;
    }
}
