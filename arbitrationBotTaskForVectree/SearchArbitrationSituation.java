package arbitrationBotTaskForVectree;

import com.google.gson.JsonObject;

import java.lang.reflect.Array;
import java.util.*;
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

                new ParsingThisSiuation(orderBooksJava).parsingThisSiuation(orderBook);

                return null;
            };


            FutureTask<List> future;

            //поток получающий объект, ставим в executorServis в очередь,
            //результат выполнения которого получит future
            future = (FutureTask<List>) executorService.submit(callable);

            resultParsing.put(currencyPair, future);
        }

    }


    public static void setOrderBooksJava(ConcurrentHashMap<String, OrderBookJava> orderBooksJava) {
        SearchArbitrationSituation.orderBooksJava = orderBooksJava;
    }
}

class ParsingThisSiuation{

    private Map<String, OrderBookJava> orderBooksJava;

    public ParsingThisSiuation(Map orderBooksJava){
        this.orderBooksJava = orderBooksJava;
    }


    public List parsingThisSiuation(OrderBookJava orderBookJava){
        return parsingThisSiuation(orderBookJava, true);
    }

    //метод который парсит данную ситуацию
    private List parsingThisSiuation(OrderBookJava orderBookJava, boolean recursion) {


        String nameСurrency = orderBookJava.getNameСurrency();
        //разделяем названия для их последующего парсинга, по отдельности.
        String[] pairToString = returnPairToArrString(nameСurrency);

        float price = 0; //цена лучшего предложения
        float volume = 0; //и количество

        //поиск лучшего предложения
        for (float variablePrice : orderBookJava.getAsks().keySet()){


            if (price == 0){
                price = variablePrice;
            }

            if (price > variablePrice){
                price = variablePrice;
                volume = (float) orderBookJava.getAsks().get(variablePrice);
            }
        }

        String nameOffer = pairToString[1]; //имя валюты на которая была лучшее предложение

        if (recursion){

            //коллекция предложний валют, которые соответствуют требованиям.
            ArrayList<OrderBookJava> listOrderBook = new ArrayList<>();

            //парсим все пары валют на соответствие с этим именем
            for (String currencyPair : orderBooksJava.keySet()){

                //пропускаем валюту для которого проводится этот парсинг
                if(nameСurrency.equals(currencyPair)){ break; }

                //пропускаем пары валют, в которых не входит нужная нам валюта
                if (!currencyPair.equals(nameOffer)) {break; }

                //добавляем в коллекцию все предложения валют, которые соответсвуют требованиям.
                listOrderBook.add(orderBooksJava.get(currencyPair));
            }


            System.out.println(nameOffer);
            System.out.println(nameСurrency);

        }






        return null;
    }

    //метод который из пары валют делит на валюты, для их последующих сравнений
    private String[] returnPairToArrString(String pairCurrency) {
        return pairCurrency.split("_");
    }

}
