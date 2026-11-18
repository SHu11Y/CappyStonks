import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** Sorts and filters market information for all stocks. */
public class MarketScreener {

    public static class Row {
        public final String ticker;
        public final double price;
        public final double changePercent;
        public final double avgVolume;

        Row(String ticker, double price, double changePercent, double avgVolume) {
            this.ticker = ticker;
            this.price = price;
            this.changePercent = changePercent;
            this.avgVolume = avgVolume;
        }
    }

    private final MarketAnalyzer analyzer = new MarketAnalyzer();

    /** One row per stock for the date range (price = last close in range). */
    public List<Row> buildRows(MarketDataManager manager, LocalDate start, LocalDate end) {
        List<Row> rows = new ArrayList<>();
        for (Stock s : manager.getAllStocks()) {
            MarketAnalyzer.Result r = analyzer.analyze(s, start, end);
            if (r == null) continue;
            List<PriceRecord> range = s.getRecordsBetween(start, end);
            double lastClose = range.get(range.size() - 1).getClose();
            rows.add(new Row(s.getTicker(), lastClose, r.periodReturn, r.averageVolume));
        }
        return rows;
    }

    /** Keeps rows within the price range and above the minimum average volume. */
    public List<Row> filter(List<Row> rows, double minPrice, double maxPrice, double minVolume) {
        List<Row> result = new ArrayList<>();
        for (Row r : rows) {
            if (r.price >= minPrice && r.price <= maxPrice && r.avgVolume >= minVolume) {
                result.add(r);
            }
        }
        return result;
    }

    /** sortBy: 1 = ticker, 2 = price, 3 = percentage change, 4 = volume. */
    public void sort(List<Row> rows, int sortBy, boolean ascending) {
        Comparator<Row> cmp;
        switch (sortBy) {
            case 2:  cmp = Comparator.comparingDouble(r -> r.price); break;
            case 3:  cmp = Comparator.comparingDouble(r -> r.changePercent); break;
            case 4:  cmp = Comparator.comparingDouble(r -> r.avgVolume); break;
            default: cmp = Comparator.comparing(r -> r.ticker); break;
        }
        if (!ascending) cmp = cmp.reversed();
        rows.sort(cmp);
    }

    public void display(List<Row> rows) {
        System.out.println();
        System.out.printf("%-8s %12s %12s %16s%n", "Ticker", "Price", "Change", "Avg Volume");
        System.out.println("-".repeat(52));
        if (rows.isEmpty()) {
            System.out.println("(no stocks match)");
            return;
        }
        for (Row r : rows) {
            System.out.printf("%-8s %12s %12s %,16d%n", r.ticker,
                    String.format("$%.2f", r.price),
                    String.format("%+.2f%%", r.changePercent),
                    Math.round(r.avgVolume));
        }
    }
}
