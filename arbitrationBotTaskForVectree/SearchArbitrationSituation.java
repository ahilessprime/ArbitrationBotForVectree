package arbitrationBotTaskForVectree;

import java.util.concurrent.ConcurrentHashMap;

public class SearchArbitrationSituation {

    private static ConcurrentHashMap<String, OrderBookJava> orderBooksJava;

    public SearchArbitrationSituation(){};
    public SearchArbitrationSituation(ConcurrentHashMap<String,OrderBookJava> orderBooksJava){
        this.orderBooksJava = orderBooksJava;
    }

    public void startParsing(){

    }

    public static void setOrderBooksJava(ConcurrentHashMap<String, OrderBookJava> orderBooksJava) {
        SearchArbitrationSituation.orderBooksJava = orderBooksJava;
    }
}
