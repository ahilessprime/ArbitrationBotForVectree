package arbitrationBotTaskForVectree;

import com.google.gson.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.*;


/**
 *
 * class ConnectAndParing подкючается к инету, получает данные по массиву поченных ссылок.
 *
 * конструктор public ConnectAndParsing(ArrayList<URL>) запускает метод parsing()
 *
 * метод public parsing(ArrayList<URL>) получив колекцию ссылок, парсит их, получая объекты.
 *
 * метод public HashMap<String, JsonObject> getResultParsing() возвращает коллекцию с ключами "пар валют",
 * и JsonObject-ами, полученные при парсинге ссылок.
 */


public class ConnectAndParsing {

    //массив содержащий в себе валюты
    //нужен для наименований ключей в Map
    private Currencies currencies = new Currencies();
    //массив содержит в себе полученные результаты парсинга
    private HashMap<String, FutureTask<JsonObject>> resultParsing = new HashMap<>();

    //менеджер потоков на 20 потоков
    ExecutorService executorService = Executors.newFixedThreadPool(20);


    public ConnectAndParsing(){}
    //конструктор принимающий коллекцию URL для парсинга и запускающий парсинг
    public ConnectAndParsing(ArrayList<URL> urlList) {
        parsing(urlList);
    }


    //метод принимающий коллекцию URL и парсящий их.
    public void parsing(ArrayList<URL> urlList){


        //запускаем цикл по каждому из пар валют
        for (int i = 0; i < currencies.size(); i++){

            //подключаемся в новом потоке,
            //проверяем полученный объект,
            //если все в порядке, добавляем в коллекцию,


            //получаем ссылку на конкретную пару валют
            URL url = urlList.get(i);
            //переменная i что бы использовать в лямбде
            int  finalI = i;


            //получаем содержимое ссылки в новом потоке, в виде JsonObject
            Callable<JsonObject> callable = () -> {


                //шкала загрузки всех пар валют в процентах
                System.out.print(" " +(finalI *  100/currencies.size())+"%\r");
                if (finalI == currencies.size()-1){
                    System.out.println(" 100% - все данные загруженны.\n");
                }


                //подключаемся по ссылке и принимаем содержимые объекты.
                //копирайт, в деталях не разобрался, если кто нибудь объяснит к чему все это,
                //и почемы стримы не были закрыты в явном виде, буду очень благодарен.
                StringBuffer response = new StringBuffer();

                try{
                    BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
                    String inputLine;

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                }catch (IOException e)
                {
                    e.printStackTrace(System.err);
                }


                JsonObject json = new Gson().fromJson(response.toString(), JsonObject.class);
                return json;
            };

            FutureTask<JsonObject> future;

            //поток получающий объект, ставим в executorServis в очередь,
            //результат выполнения которого получит future
            future =(FutureTask<JsonObject>) executorService.submit(callable);

            //и этот объект в форме future, передаем в коллекцию, под ключем данной пары валюты
            resultParsing.put(currencies.get(i), future);

        }

        //завершаем ожидание новых задач.
        executorService.shutdown();


    }


    //метод возвращающий коллекцию готовых результатов парсинга, в виде ключей парвалют и их JsonObject-тов
    public HashMap<String, JsonObject> getResultParsing(){

        //ожидаем пока Executor не завершится, выполнив все задачи
        //или до истечения 3 минут
        try {
            executorService.awaitTermination(3, TimeUnit.MINUTES);
        }catch (InterruptedException e){e.printStackTrace(System.err);}


        //новая коллекция с приведенными типами, которая будет возвращена методом
        HashMap<String, JsonObject> OrderBook = new HashMap<>();

        //проверяем, все ли ключи коллекции, содержат нужные данные
        for (String key : currencies){
            FutureTask<JsonObject> future = resultParsing.get(key);
            String string=null; //переменная string получит себе данные из future
            try{ string = future.get().toString();}
            catch (InterruptedException e){e.printStackTrace(System.err);}
            catch(ExecutionException e){e.printStackTrace(System.err);}
            catch (NullPointerException e){string=null;}

            if (string == null){ //если переменная ничего не содержит
                resultParsing.remove(key); //удаляем из коллекции его ключ
                System.err.println("return null - "+key);
            }

            else if (string.contains("error")){ //если переменная содержит сообщение об ошибке
                resultParsing.remove(key); //удаляем из коллкции его ключ
                System.err.println("return error - "+key);
            }

            else{ //если все в порядке
                try{
                OrderBook.put(key, future.get());} //добовляем в коллецию с ключем
                catch (InterruptedException e){e.printStackTrace(System.err);}
                catch (ExecutionException e){e.printStackTrace(System.err);}

            }
        }


        //возвращаем готовую, приведенную к JsonObject коллекцию.
        return OrderBook;
    }


}
