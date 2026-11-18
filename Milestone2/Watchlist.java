import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/** The user's personal watchlist. Saved to watchlist.txt so it survives restarts. */
public class Watchlist {
    private static final Path FILE = Paths.get("watchlist.txt");
    private final Set<String> tickers = new LinkedHashSet<>();

    public Watchlist() { load(); }

    public boolean add(String ticker) {
        boolean added = tickers.add(ticker.toUpperCase());
        if (added) save();
        return added;
    }

    public boolean remove(String ticker) {
        boolean removed = tickers.remove(ticker.toUpperCase());
        if (removed) save();
        return removed;
    }

    public boolean contains(String ticker) { return tickers.contains(ticker.toUpperCase()); }

    public boolean isEmpty() { return tickers.isEmpty(); }

    public List<String> getTickers() { return new ArrayList<>(tickers); }

    public void display(MarketDataManager manager) {
        System.out.println();
        System.out.println("MY WATCHLIST");
        System.out.printf("%-8s %-26s %s%n", "Ticker", "Latest Available Price", "Change");
        System.out.println("--------------------------------------------------");
        if (tickers.isEmpty()) {
            System.out.println("(your watchlist is empty)");
            return;
        }
        for (String t : tickers) {
            Stock s = manager.getStock(t);
            if (s == null) {
                System.out.printf("%-8s %s%n", t, "no data available");
                continue;
            }
            System.out.printf("%-8s %-26s %+.2f%%%n", t,
                    String.format("$%.2f", s.getLatestRecord().getClose()),
                    s.getLatestChangePercent());
        }
    }

    private void load() {
        try {
            if (Files.exists(FILE)) {
                for (String line : Files.readAllLines(FILE)) {
                    if (!line.trim().isEmpty()) tickers.add(line.trim().toUpperCase());
                }
            }
        } catch (IOException e) {
            System.out.println("Could not load watchlist: " + e.getMessage());
        }
    }

    private void save() {
        try {
            Files.write(FILE, tickers);
        } catch (IOException e) {
            System.out.println("Could not save watchlist: " + e.getMessage());
        }
    }
}
