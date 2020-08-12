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


            //Callable<List> callable = () -> {

                new ParsingThisSiuation(orderBooksJava).parsing(orderBook);

            //    return null;
            //};


            //FutureTask<List> future;

            //поток получающий объект, ставим в executorServis в очередь,
            //результат выполнения которого получит future
            //future = (FutureTask<List>) executorService.submit(callable);

           // resultParsing.put(currencyPair, future);
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


    public ArrayList parsing(OrderBookJava orderBookJava){
        return parsing(orderBookJava, true);
    }

    //метод который парсит данную ситуацию
    private ArrayList parsing(OrderBookJava orderBookJava, boolean recursion) {


        String nameСurrency = orderBookJava.getNameСurrency();
        //разделяем названия для их последующего парсинга, по отдельности.
        String[] pairToString = returnPairToArrString(nameСurrency);

        //поиск лучшего предложения
        float[] arrfloat = bestOfferScanAsks(orderBookJava);
        float price = arrfloat[0]; //цена лучшего предложения
        float volume = arrfloat[1]; //и его количество

        System.out.println(price+" luli "+volume);


        String nameOffer = pairToString[1]; //имя валюты на которая была лучшее предложение

        if (recursion){

            //коллекция предложений валют, которые соответствуют требованиям.
            ArrayList<OrderBookJava> listOrderBook = new ArrayList<>();

            System.out.println("имя "+nameСurrency);
            System.out.println("ищем "+nameOffer);

            //парсим все пары валют на соответствие с этим именем
            for (String currencyPair : orderBooksJava.keySet()){

                System.out.println("очередь "+currencyPair);

                //пропускаем валюту для которого проводится этот парсинг
                if(nameСurrency.equals(currencyPair)){System.out.println("та же валюта "+currencyPair); continue;}

                //пропускаем пары валют, в которых не входит нужная нам валюта
                if ((currencyPair.indexOf(nameOffer)) == -1) {System.out.println("пропуск "+nameOffer+" - "+ currencyPair); continue; }

                else {
                    //проверяем на то, точно ли совпадает валюта
                    String[] str = returnPairToArrString(currencyPair);
                    boolean matches = false;
                    for (String s:str){
                        if (!matches){
                            matches = nameOffer.equals(s);
                        }
                    }
                    //если не совпадает, возвращаемся к парсингу
                    if (!matches){
                        System.out.println("не совпадает "+nameOffer+" "+currencyPair );
                        continue;
                    }

                    //иначе продолжаем
                    System.out.println("добавляем в список "+orderBooksJava.get(currencyPair) );
                    //добавляем в коллекцию предложение валют, которые соответсвуют требованиям.
                    listOrderBook.add(orderBooksJava.get(currencyPair));
                }
            }

            ArrayList<ArrayList> recursiveArrList = new ArrayList<>();

            //а теперь, нужно отпарсить валюты, соответстыующие парсингу
            for (OrderBookJava orderRecursive : listOrderBook){
                recursiveArrList.add(parsing(orderRecursive,false));
            }



            for (OrderBookJava ord: listOrderBook){
                System.out.println(ord.getNameСurrency());
            }

        }
        else{
            
        }






        return null;
    }

    //метод который из пары валют делит на валюты, для их последующих сравнений
    private String[] returnPairToArrString(String pairCurrency) {
        return pairCurrency.split("_");
    }

    //метод, ищущий лучшее предложение продажи
    private float[] bestOfferScanAsks(OrderBookJava orderBookJava){
        float price = 0; //цена лучшего предложения
        float volume = 0; //и количество

        //поиск лучшего предложения
        for (float variablePrice : orderBookJava.getAsks().keySet()){


            if (price == 0){
                price = variablePrice;
            }

            if (price > variablePrice){
                price = variablePrice;
                volume =  orderBookJava.getAsks().get(variablePrice);

            }
        }

        float[] arr = new float[]{price,volume};

        return arr;
    }

}
