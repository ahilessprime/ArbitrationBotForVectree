package arbitrationBotTaskForVectree;

import java.util.HashMap;

public class OrderBookJava {
    private String nameСurrency;
    private HashMap<Float, Float> asks = new HashMap<>();
    private HashMap<Float, Float> bids = new HashMap<>();

    public OrderBookJava(String nameСurrency, HashMap asks, HashMap bids ){
        this.nameСurrency = nameСurrency;
        this.asks = asks;
        this.bids = bids;
    }



    public void setNameСurrency(String nameСurrency) {
        this.nameСurrency = nameСurrency; }

    public void setAsks(HashMap<Float, Float> asks) {
        this.asks = asks; }

    public void setBids(HashMap<Float, Float> bids) {
        this.bids = bids; }



    public String getNameСurrency() {
        return nameСurrency;
    }

    public HashMap<Float, Float> getAsks() {
        return asks;
    }

    public HashMap<Float, Float> getBids() {
        return bids;
    }

    @Override
    public String toString() {
        return ("name: "+nameСurrency+"\n asks: "+asks.toString()+
                "\n bids: "+bids.toString());
    }
}
