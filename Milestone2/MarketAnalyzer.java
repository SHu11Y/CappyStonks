import java.time.LocalDate;
import java.util.List;

/** Performs calculations for a selected date range. */
public class MarketAnalyzer {

    /** Holds the results of one analysis. */
    public static class Result {
        public String ticker;
        public LocalDate start, end;
        public double highestClose, lowestClose, averageClose, periodReturn;
        public double averageVolume;
        public LocalDate bestDay, worstDay;
        public double bestDayChange, worstDayChange;
        public boolean hasDailyChanges;
    }

    /** Returns null when the stock has no data in the range. */
    public Result analyze(Stock stock, LocalDate start, LocalDate end) {
        List<PriceRecord> all = stock.getRecords();
        List<PriceRecord> range = stock.getRecordsBetween(start, end);
        if (range.isEmpty()) return null;

        Result r = new Result();
        r.ticker = stock.getTicker();
        r.start = range.get(0).getDate();
        r.end = range.get(range.size() - 1).getDate();
        r.highestClose = Double.NEGATIVE_INFINITY;
        r.lowestClose = Double.POSITIVE_INFINITY;

        double sumClose = 0;
        double sumVolume = 0;
        for (PriceRecord p : range) {
            r.highestClose = Math.max(r.highestClose, p.getClose());
            r.lowestClose = Math.min(r.lowestClose, p.getClose());
            sumClose += p.getClose();
            sumVolume += p.getVolume();
        }
        r.averageClose = sumClose / range.size();
        r.averageVolume = sumVolume / range.size();

        double first = range.get(0).getClose();
        double last = range.get(range.size() - 1).getClose();
        r.periodReturn = (last - first) / first * 100.0;

        // Daily change compares with the previous available trading day.
        r.bestDayChange = Double.NEGATIVE_INFINITY;
        r.worstDayChange = Double.POSITIVE_INFINITY;
        for (int i = 1; i < all.size(); i++) {
            PriceRecord today = all.get(i);
            if (today.getDate().isBefore(start) || today.getDate().isAfter(end)) continue;
            double prev = all.get(i - 1).getClose();
            double change = (today.getClose() - prev) / prev * 100.0;
            r.hasDailyChanges = true;
            if (change > r.bestDayChange) { r.bestDayChange = change; r.bestDay = today.getDate(); }
            if (change < r.worstDayChange) { r.worstDayChange = change; r.worstDay = today.getDate(); }
        }
        return r;
    }
}
