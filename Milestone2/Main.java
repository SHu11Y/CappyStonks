import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/** Text-based user interface for the Mini Market Terminal. */
public class Main {
    private static final Scanner in = new Scanner(System.in);
    private static final MarketDataManager manager = new MarketDataManager();
    private static final MarketAnalyzer analyzer = new MarketAnalyzer();
    private static final StockComparator comparator = new StockComparator();
    private static final MarketScreener screener = new MarketScreener();
    private static Watchlist watchlist;

    public static void main(String[] args) {
        String dataFolder = args.length > 0 ? args[0] : "data";
        int loaded = manager.loadData(dataFolder);
        if (loaded == 0) {
            System.out.println("No stock data found in folder '" + dataFolder + "'.");
            System.out.println("Keep the data folder in the project root (or pass its path as an argument).");
            return;
        }
        watchlist = new Watchlist();
        System.out.println("Loaded " + loaded + " stocks: " + manager.getTickers());

        boolean running = true;
        while (running) {
            printMenu();
            switch (readLine("Select an option: ")) {
                case "1": searchStock(); break;
                case "2": viewHistoricalData(); break;
                case "3": analyzeStock(); break;
                case "4": compareStocks(); break;
                case "5": watchlist.display(manager); break;
                case "6": manageWatchlist(); break;
                case "7": sortAndFilter(); break;
                case "0": running = false; break;
                default: System.out.println("Invalid option. Please try again.");
            }
        }
        System.out.println("Goodbye!");
    }

    private static void printMenu() {
        System.out.println();
        System.out.println("================================================");
        System.out.println("MINI MARKET TERMINAL");
        System.out.println("================================================");
        System.out.println("1. Search Stock");
        System.out.println("2. View Historical Data");
        System.out.println("3. Analyze Stock");
        System.out.println("4. Compare Stocks");
        System.out.println("5. View Watchlist");
        System.out.println("6. Manage Watchlist");
        System.out.println("7. Sort & Filter Market");
        System.out.println("0. Exit");
    }

    // ---------- menu actions ----------

    private static void searchStock() {
        Stock s = askStock("Enter ticker symbol: ");
        if (s == null) return;
        PriceRecord first = s.getFirstRecord();
        PriceRecord last = s.getLatestRecord();
        System.out.println();
        System.out.println("Ticker: " + s.getTicker());
        System.out.println("Records: " + s.getRecords().size() + " (" + first.getDate() + " to " + last.getDate() + ")");
        System.out.printf("Latest close: $%.2f (%+.2f%%)%n", last.getClose(), s.getLatestChangePercent());
        System.out.println("On watchlist: " + (watchlist.contains(s.getTicker()) ? "yes" : "no"));
    }

    private static void viewHistoricalData() {
        Stock s = askStock("Enter ticker symbol: ");
        if (s == null) return;
        LocalDate[] range = askDateRange(s);
        if (range == null) return;

        List<PriceRecord> records = new ArrayList<>(s.getRecordsBetween(range[0], range[1]));
        if (records.isEmpty()) {
            System.out.println("No records in that period.");
            return;
        }
        System.out.println("Sort by: 1) Date  2) Close price  3) Volume  (Enter = date)");
        String choice = readLine("Choice: ");
        boolean desc = false;
        if (choice.equals("2") || choice.equals("3")) {
            desc = readLine("Highest first? (y/n): ").equalsIgnoreCase("y");
        } else {
            desc = readLine("Newest first? (y/n): ").equalsIgnoreCase("y");
        }
        final boolean descending = desc;
        if (choice.equals("2")) {
            records.sort((a, b) -> Double.compare(a.getClose(), b.getClose()));
        } else if (choice.equals("3")) {
            records.sort((a, b) -> Long.compare(a.getVolume(), b.getVolume()));
        } else {
            records.sort((a, b) -> a.getDate().compareTo(b.getDate()));
        }
        if (descending) java.util.Collections.reverse(records);

        System.out.println();
        for (PriceRecord r : records) System.out.println(r);
        System.out.println(records.size() + " record(s).");
    }

    private static void analyzeStock() {
        Stock s = askStock("Enter ticker symbol: ");
        if (s == null) return;
        LocalDate[] range = askDateRange(s);
        if (range == null) return;

        MarketAnalyzer.Result r = analyzer.analyze(s, range[0], range[1]);
        if (r == null) {
            System.out.println("No records in that period.");
            return;
        }
        System.out.println();
        System.out.println("Ticker: " + r.ticker);
        System.out.println("Period: " + r.start + " - " + r.end);
        System.out.printf("Highest Closing Price: $%.2f%n", r.highestClose);
        System.out.printf("Lowest Closing Price: $%.2f%n", r.lowestClose);
        System.out.printf("Average Closing Price: $%.2f%n", r.averageClose);
        System.out.printf("Price Change: %+.2f%%%n", r.periodReturn);
        System.out.printf("Average Trading Volume: %,d%n", Math.round(r.averageVolume));
        if (r.hasDailyChanges) {
            System.out.printf("Best Trading Day: %s (%+.2f%%)%n", r.bestDay, r.bestDayChange);
            System.out.printf("Worst Trading Day: %s (%+.2f%%)%n", r.worstDay, r.worstDayChange);
        }
    }

    private static void compareStocks() {
        System.out.println("Available: " + manager.getTickers());
        String line = readLine("Enter at least two tickers separated by spaces or commas: ");
        List<Stock> chosen = new ArrayList<>();
        for (String t : line.split("[,\\s]+")) {
            if (t.isEmpty()) continue;
            Stock s = manager.getStock(t);
            if (s == null) {
                System.out.println("Unknown ticker: " + t.toUpperCase());
                return;
            }
            if (!chosen.contains(s)) chosen.add(s);
        }
        if (chosen.size() < 2) {
            System.out.println("Please choose at least two different stocks.");
            return;
        }
        LocalDate[] range = askDateRange(chosen.get(0));
        if (range == null) return;
        comparator.compare(chosen, range[0], range[1]);
    }

    private static void manageWatchlist() {
        System.out.println("1. Add stock  2. Remove stock  0. Back");
        String choice = readLine("Select: ");
        if (choice.equals("1")) {
            Stock s = askStock("Ticker to add: ");
            if (s == null) return;
            System.out.println(watchlist.add(s.getTicker())
                    ? s.getTicker() + " added to watchlist."
                    : s.getTicker() + " is already on your watchlist.");
        } else if (choice.equals("2")) {
            String t = readLine("Ticker to remove: ");
            System.out.println(watchlist.remove(t)
                    ? t.toUpperCase() + " removed from watchlist."
                    : t.toUpperCase() + " is not on your watchlist.");
        }
    }

    private static void sortAndFilter() {
        LocalDate[] range = askDateRange(null);
        if (range == null) return;
        List<MarketScreener.Row> rows = screener.buildRows(manager, range[0], range[1]);

        System.out.println("Filter (press Enter to skip any):");
        double minPrice = askDouble("  Minimum price: ", 0);
        double maxPrice = askDouble("  Maximum price: ", Double.MAX_VALUE);
        double minVolume = askDouble("  Minimum average volume: ", 0);
        rows = screener.filter(rows, minPrice, maxPrice, minVolume);

        System.out.println("Sort by: 1) Ticker  2) Price  3) % Change  4) Volume");
        String choice = readLine("Choice (Enter = ticker): ");
        int sortBy = choice.matches("[1-4]") ? Integer.parseInt(choice) : 1;
        boolean ascending = !readLine("Highest/Z first? (y/n): ").equalsIgnoreCase("y");
        screener.sort(rows, sortBy, ascending);
        screener.display(rows);
    }

    // ---------- input helpers ----------

    private static String readLine(String prompt) {
        System.out.print(prompt);
        return in.hasNextLine() ? in.nextLine().trim() : "0";
    }

    private static Stock askStock(String prompt) {
        System.out.println("Available: " + manager.getTickers());
        String t = readLine(prompt);
        Stock s = manager.getStock(t);
        if (s == null) System.out.println("Ticker '" + t.toUpperCase() + "' not found.");
        return s;
    }

    private static double askDouble(String prompt, double defaultValue) {
        while (true) {
            String text = readLine(prompt);
            if (text.isEmpty()) return defaultValue;
            try {
                return Double.parseDouble(text.replace(",", ""));
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number.");
            }
        }
    }

    /** Asks for start/end (yyyy-MM-dd). Enter = full range. Returns null on invalid input. */
    private static LocalDate[] askDateRange(Stock s) {
        LocalDate minDate = LocalDate.MIN, maxDate = LocalDate.MAX;
        if (s != null) {
            minDate = s.getFirstRecord().getDate();
            maxDate = s.getLatestRecord().getDate();
            System.out.println("Data available: " + minDate + " to " + maxDate);
        }
        try {
            String a = readLine("Start date (yyyy-MM-dd, Enter = earliest): ");
            String b = readLine("End date (yyyy-MM-dd, Enter = latest): ");
            LocalDate start = a.isEmpty() ? minDate : LocalDate.parse(a);
            LocalDate end = b.isEmpty() ? maxDate : LocalDate.parse(b);
            if (start.isAfter(end)) {
                System.out.println("Start date must be on or before the end date.");
                return null;
            }
            return new LocalDate[]{start, end};
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date. Use the format yyyy-MM-dd, e.g. 2025-03-31.");
            return null;
        }
    }
}
