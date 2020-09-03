package arbitrationBotTaskForVectree.utilSearchArbitrSit;

import arbitrationBotTaskForVectree.OrderBookJava;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

enum Rinok{Asks, Bids}

public class testMain {

    //здесь хранится весь список объектов
    private HashMap<String, OrderBookJava> mapOrderBooksJava;

    //здесь хранятся коллекции отпарсированных объектов
    ArrayList<ArrayList<StupenInfo>> goodList = new ArrayList<>();

    //констрктор принимающий список объектов
    public testMain(HashMap<String, OrderBookJava> orderBookJava){
        this.mapOrderBooksJava = orderBookJava;
    }

    //метод запускающий парсинг всех объектов
    public void startPasing(){

        //парсим каждый объект из списка
        for (String currencyPair : mapOrderBooksJava.keySet()){
            OrderBookJava orderBookJava = mapOrderBooksJava.get(currencyPair);
            ArrayList<StupenInfo> arbitrationSituation = parsingOrderBook(orderBookJava);

            //теперь нужен метод, который проверяет арбитражную ситуацию на действительность
            //и наличию ошибок.
            if (verificationArbSit(arbitrationSituation)){

            }

            //в случае все ок, добавляем в коллекцию goodList
        }


    }

    //парсит принятый объект, на арбитражную ситуацию
    private ArrayList<StupenInfo> parsingOrderBook(OrderBookJava orderBookjava){

        //разделяем имя валютной пары, что бы понимать, с чем имеем дело.
        String [] pairToStringStupen1 = returnPairToArrString(orderBookjava.getNameСurrency());



        //поиск лучшего предложения покупки 1 ступени
        float[] priceAndVolumeStupen1Asks = bestOfferscan(Rinok.Asks, orderBookjava);

        //коллекция валют, с которыми можно торговать, используя данную валюту
        ArrayList<OrderBookJava> listOrderBooksStupen1
                = poiskSovpadayoushikhValut(pairToStringStupen1[1]);

        //удаляем из него пару, для которого и была созданна эта коллекция
        deleteCurrencyPairObject(listOrderBooksStupen1, orderBookjava.getNameСurrency());



        //поиск лучшего предложения из 2-рой ступени
        OrderBookJava orderBook2stupen = null;
        float [] priceAndVolumeStupen2Asks = new float[]{};
        float [] priceAndVolumeStupen2Bids = new float[]{};

        for (OrderBookJava orderBook: listOrderBooksStupen1){

            String [] pairToStringStupen2 = returnPairToArrString(orderBook.getNameСurrency());

            //если валюта распологается вторым, в паре валют
            if (pairToStringStupen1[1].equals(pairToStringStupen2[1])){

                float [] priceAndVolumeStupen3Asks = bestOfferscan(Rinok.Asks, orderBook);
                float [] priceAndVolumeStupen3Bids = bestOfferscan(Rinok.Bids, orderBook);

                //можем ли мы, продать их с выгодой
                if (priceAndVolumeStupen3Asks[0]< priceAndVolumeStupen3Bids[0]){
                    continue; //если нет, то не зачем нам такая торговля.
                }

                //инициализация
                if(priceAndVolumeStupen2Asks.length == 0 ){

                    orderBook2stupen = orderBook;
                    priceAndVolumeStupen2Asks = priceAndVolumeStupen3Asks;
                    priceAndVolumeStupen2Bids = priceAndVolumeStupen3Bids;

                }

                //поиск лучшего варианта, арбитражной ситуации
                float profit = priceAndVolumeStupen2Bids[0] - priceAndVolumeStupen2Bids[0];


                if (profit <
                        (priceAndVolumeStupen3Asks[0] - priceAndVolumeStupen3Bids[0])){
                    orderBook2stupen = orderBook;
                    priceAndVolumeStupen2Asks = priceAndVolumeStupen3Asks;
                    priceAndVolumeStupen2Bids = priceAndVolumeStupen3Bids;
                }

            }else{ //если валюта распологается в обратном порядке, в паре валют
                //потом надо что нить придумать для этого случая
                continue;
            }
        }

        //поиск лучшего предложения продажи 1 ступени
        float[] priceAndVolumeStupen1Bids = bestOfferscan(Rinok.Bids, orderBookjava);


        // теперь нужен list, возвращающий трассу наилучших  манипуляций.
        ArrayList<StupenInfo> traceProdaj = new ArrayList<>();

        //добавляем в коллекцию 1 ступень
        traceProdaj.add( new StupenInfo(orderBookjava, priceAndVolumeStupen1Asks, priceAndVolumeStupen1Bids));
        //добавляем вторую ступень
        traceProdaj.add( new StupenInfo(orderBook2stupen, priceAndVolumeStupen2Asks, priceAndVolumeStupen2Bids));

        return traceProdaj;

    }


    //метод, удаляющий из коллекции listOrderBooks объект, по имени пары-валюты
    private void deleteCurrencyPairObject(ArrayList<OrderBookJava> listOrderBooks,
                                          String currencyPair){
        for (OrderBookJava orderBook : listOrderBooks){
            if (currencyPair.equals(orderBook.getNameСurrency())){
                listOrderBooks.remove(orderBook);
            }
        }
    }


    //метод разделяет пары валют, на отдельные валюты
    private String[] returnPairToArrString(String pairCurrency){
        return pairCurrency.split("_");
    }


    //метод ищущий пары с нужным валютой
    private ArrayList<OrderBookJava> poiskSovpadayoushikhValut(String currency){
        ArrayList<OrderBookJava> listOrderBooks = new ArrayList<>();

        //парсим все пары валют на соответствие с этим именем
        for (String currencyPair : mapOrderBooksJava.keySet()) {



            //пропускаем пары валют, в которых не входит нужная нам валюта
            if ((currencyPair.indexOf(currency)) == -1) {
                continue;
            } else {
                //проверяем на то, точно ли совпадает валюта
                String[] str = returnPairToArrString(currencyPair);
                boolean matches = false;
                for (String s : str) {
                    if (!matches) {
                        matches = currency.equals(s);
                    }
                }
                //если не совпадает, возвращаемся к парсингу
                if (!matches) {
                    continue;
                }

                //иначе продолжаем
                //добавляем в коллекцию предложение валют, которые соответсвуют требованиям.
                listOrderBooks.add(mapOrderBooksJava.get(currencyPair));
            }
        }

        return listOrderBooks;
    }

    //метод ищущий лучшие предложения продажи или купли
    private float[] bestOfferscan(Rinok predlojenie, OrderBookJava orderBookJava){
        float price = 0; //цена лучшего предложения
        float volume = 0; //и его количество

        switch (predlojenie){
            case Asks:

                //поиск лучшего предложения покупки
                for (float variablePrice : orderBookJava.getAsks().keySet()){


                    if (price == 0){
                        price = variablePrice;
                    }

                    if (price > variablePrice){
                        price = variablePrice;
                        volume =  orderBookJava.getAsks().get(variablePrice);
                    }
                }
                break;

            case Bids:


                for (float variablePrice : orderBookJava.getBids().keySet()){


                    if (price == 0){
                        price = variablePrice;
                    }

                    if (price < variablePrice){
                        price = variablePrice;
                        volume =  orderBookJava.getBids().get(variablePrice);
                    }
                }
                break;
        }

        float[] priceAndVolume = new float[]{price,volume};
        return priceAndVolume;
    }

    //метод проверяющий на действительность арбитражную ситуации
    private boolean verificationArbSit(ArrayList<StupenInfo> arbitrationSituation){


    }
}

class StupenInfo{

    private OrderBookJava orderBookJava;

    private float [] priceAndVolumeAsks;
    private float [] priceAndVolumeBids;

    public StupenInfo(OrderBookJava orderBookJava,
                      float [] priceAndVolumeAsks, float [] priceAndVolumeBids){
        this.orderBookJava = orderBookJava;
        this.priceAndVolumeAsks = priceAndVolumeAsks;
        this.priceAndVolumeBids = priceAndVolumeBids;
    }

    public OrderBookJava getOrderBookJava() {
        return orderBookJava;
    }

    public float[] getPriceAndVolumeAsks() {
        return priceAndVolumeAsks;
    }

    public float[] getPriceAndVolumeBids() {
        return priceAndVolumeBids;
    }
}
