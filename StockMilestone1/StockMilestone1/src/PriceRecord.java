import java.time.LocalDate;

public class PriceRecord {
    private final LocalDate date;
    private final double open;
    private final double high;
    private final double low;
    private final double close;
    private final long volume;

    public PriceRecord(LocalDate date, double open, double high, double low,
                       double close, long volume) {
        this.date = date;
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.volume = volume;
    }

    public LocalDate getDate() { return date; }
    public double getOpen() { return open; }
    public double getHigh() { return high; }
    public double getLow() { return low; }
    public double getClose() { return close; }
    public long getVolume() { return volume; }

    @Override
    public String toString() {
        return String.format("%s  Open: $%.2f  High: $%.2f  Low: $%.2f  Close: $%.2f  Volume: %,d",
                date, open, high, low, close, volume);
    }
}
