import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** Stores a ticker symbol and its historical records. */
public class Stock {
    private final String ticker;
    private final List<PriceRecord> records = new ArrayList<>();

    public Stock(String ticker) {
        this.ticker = ticker.toUpperCase();
    }

    public String getTicker() { return ticker; }

    public void addRecord(PriceRecord record) { records.add(record); }

    public void sortRecords() {
        records.sort(Comparator.comparing(PriceRecord::getDate));
    }

    public List<PriceRecord> getRecords() { return records; }

    /** Records with start <= date <= end (inclusive). */
    public List<PriceRecord> getRecordsBetween(LocalDate start, LocalDate end) {
        List<PriceRecord> result = new ArrayList<>();
        for (PriceRecord r : records) {
            if (!r.getDate().isBefore(start) && !r.getDate().isAfter(end)) {
                result.add(r);
            }
        }
        return result;
    }

    public PriceRecord getFirstRecord() {
        return records.isEmpty() ? null : records.get(0);
    }

    public PriceRecord getLatestRecord() {
        return records.isEmpty() ? null : records.get(records.size() - 1);
    }

    /** Latest close compared with the previous available close, in percent. */
    public double getLatestChangePercent() {
        if (records.size() < 2) return 0;
        double last = records.get(records.size() - 1).getClose();
        double prev = records.get(records.size() - 2).getClose();
        return (last - prev) / prev * 100.0;
    }
}
