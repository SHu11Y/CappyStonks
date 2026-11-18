import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Compares the performance of two or more stocks over a period. */
public class StockComparator {
    private final MarketAnalyzer analyzer = new MarketAnalyzer();

    public void compare(List<Stock> stocks, LocalDate start, LocalDate end) {
        List<MarketAnalyzer.Result> results = new ArrayList<>();
        for (Stock s : stocks) {
            MarketAnalyzer.Result r = analyzer.analyze(s, start, end);
            if (r == null) {
                System.out.println("No data for " + s.getTicker() + " in that period.");
                return;
            }
            results.add(r);
        }

        System.out.println();
        System.out.println("COMPARISON: " + start + " to " + end);
        StringBuilder header = new StringBuilder(String.format("%-16s", ""));
        for (MarketAnalyzer.Result r : results) header.append(String.format("%16s", r.ticker));
        System.out.println(header);
        System.out.println("-".repeat(header.length()));

        row("Period Return", results, r -> String.format("%+.2f%%", r.periodReturn));
        row("Average Price", results, r -> String.format("$%.2f", r.averageClose));
        row("Highest Price", results, r -> String.format("$%.2f", r.highestClose));
        row("Lowest Price", results, r -> String.format("$%.2f", r.lowestClose));
        row("Average Volume", results, r -> String.format("%,d", Math.round(r.averageVolume)));

        MarketAnalyzer.Result best = results.get(0);
        for (MarketAnalyzer.Result r : results) {
            if (r.periodReturn > best.periodReturn) best = r;
        }
        System.out.printf("%nBest performer: %s (%+.2f%%)%n", best.ticker, best.periodReturn);
    }

    private interface Getter { String get(MarketAnalyzer.Result r); }

    private void row(String label, List<MarketAnalyzer.Result> results, Getter getter) {
        StringBuilder sb = new StringBuilder(String.format("%-16s", label));
        for (MarketAnalyzer.Result r : results) sb.append(String.format("%16s", getter.get(r)));
        System.out.println(sb);
    }
}
