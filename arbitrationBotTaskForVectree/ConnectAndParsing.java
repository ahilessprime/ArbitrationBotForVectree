package arbitrationBotTaskForVectree;

import com.google.gson.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.*;

public class ConnectAndParsing {

    //массив содержащий в себе валюты
    //нужен для наименований ключей в Map
    private Currencies currencies = new Currencies();
    //массив содержитв себе полученные результат парсинга
    private HashMap<String, FutureTask<JsonObject>> resultParsing = new HashMap<>();

    ExecutorService executorService = Executors.newFixedThreadPool(20);


    public ConnectAndParsing(){}
    public ConnectAndParsing(ArrayList<URL> urlList) {
        parsing(urlList);
    }


    public void parsing(ArrayList<URL> urlList){


        for (int i = 0; i < currencies.size(); i++){

            //подключаемся в новом потоке,
            //проверяем полученный объект,
            //если все в порядке, добавляем в коллекцию,
            //позаботившись о синхронности

            //после создаем гет метод, для коллекции.
            //проверяем, все ли потоки завершенны
            //приводим футуре к жсон и возвращаем коллекцию

            //получаем ссылку
            URL url = urlList.get(i);
            //переменная i что бы использовать в лямбде
            int finalI = i;


            //получаем содержимое ссылки в виде Json
            Callable<JsonObject> callable = () -> {


                //в деталях не разобрался, если кто нибудь объяснит к чему все это,
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
            future =(FutureTask<JsonObject>) executorService.submit(callable);


            resultParsing.put(currencies.get(i), future);

        }

        executorService.shutdown();
    }


    public HashMap<String, JsonObject> getResultParsing(){

        //ожидаем пока Executor не завершится выполнив все задачи
        //или до истечения 3 минут
        try {
            executorService.awaitTermination(3, TimeUnit.MINUTES);
        }catch (InterruptedException e){e.printStackTrace(System.err);}


        //коллекция с приведенными типами, которая будет возвращена методом
        HashMap<String, JsonObject> stakanTorgov = new HashMap<>();

        //проверяем, все ли ключи коллекции, содержат нужные данные
        for (String key : currencies){
            FutureTask<JsonObject> future = resultParsing.get(key);
            String string=null; //переменная string содержит в себе данные из future
            try{ string = future.get().toString();}
            catch (InterruptedException e){e.printStackTrace(System.err);}
            catch(ExecutionException e){e.printStackTrace(System.err);}
            catch (NullPointerException e){string=null; System.err.println("null - "+key);}

            if (string == null){ //если переменная ничего не содержит
                resultParsing.remove(key); //удаляем из коллекции его ключ
            }

            else if (string.contains("error")){ //если переменная содержит сообщение об ошибке
                resultParsing.remove(key); //удаляем из коллкции его ключ
                System.err.println("error - "+key);
            }

            else{ //если все в порядке
                try{
                stakanTorgov.put(key, future.get());}
                catch (InterruptedException e){e.printStackTrace(System.err);}
                catch (ExecutionException e){e.printStackTrace(System.err);}

            }
        }




        return stakanTorgov;
    }


}
