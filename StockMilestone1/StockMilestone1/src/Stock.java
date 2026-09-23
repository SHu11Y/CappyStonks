import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Stock {
    private final String ticker;
    private final List<PriceRecord> records;

    public Stock(String ticker, List<PriceRecord> records) {
        this.ticker = ticker.toUpperCase();
        this.records = new ArrayList<>(records);
    }

    public String getTicker() { return ticker; }

    public List<PriceRecord> getRecords() {
        return Collections.unmodifiableList(records);
    }
}
