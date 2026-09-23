import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    public static void main(String[] args) {
        Path dataPath = args.length > 0 ? Paths.get(args[0]) : Paths.get("data");
        MarketDataManager manager = new MarketDataManager(dataPath);

        try {
            manager.loadAllStocks();
        } catch (Exception e) {
            System.out.println("Error loading market data: " + e.getMessage());
            System.out.println("Make sure the 'data' folder is in the project directory.");
            return;
        }

        Scanner scanner = new Scanner(System.in);
        System.out.println("====================================");
        System.out.println("   Historical Stock Data Analyzer");
        System.out.println("====================================");
        System.out.println("Available tickers: " + String.join(", ", manager.getAvailableTickers()));

        while (true) {
            System.out.print("\nEnter ticker symbol (or EXIT to quit): ");
            String ticker = scanner.nextLine().trim().toUpperCase();

            if (ticker.equals("EXIT")) break;

            Stock stock = manager.getStock(ticker);
            if (stock == null) {
                System.out.println("Invalid ticker symbol. Please choose one of the available tickers.");
                continue;
            }

            LocalDate startDate = readDate(scanner, "Enter start date (MM/dd/yyyy): ");
            LocalDate endDate = readDate(scanner, "Enter end date (MM/dd/yyyy): ");

            if (startDate.isAfter(endDate)) {
                System.out.println("Invalid date range: the start date must be before or equal to the end date.");
                continue;
            }

            try {
                MarketAnalyzer analyzer = new MarketAnalyzer(stock, startDate, endDate);
                displayAnalysis(stock, startDate, endDate, analyzer);
            } catch (IllegalArgumentException e) {
                System.out.println("Analysis error: " + e.getMessage());
                continue;
            }

            System.out.print("\nShow historical records for this period? (Y/N): ");
            if (scanner.nextLine().trim().equalsIgnoreCase("Y")) {
                try {
                    MarketAnalyzer analyzer = new MarketAnalyzer(stock, startDate, endDate);
                    System.out.println("\nHistorical Records for " + ticker);
                    System.out.println("------------------------------------");
                    for (PriceRecord record : analyzer.getRecords()) {
                        System.out.println(record);
                    }
                } catch (IllegalArgumentException e) {
                    System.out.println("Unable to display records: " + e.getMessage());
                }
            }
        }

        scanner.close();
        System.out.println("Program ended.");
    }

    private static LocalDate readDate(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return LocalDate.parse(input, DATE_FORMAT);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date. Please use MM/dd/yyyy.");
            }
        }
    }

    private static void displayAnalysis(Stock stock, LocalDate startDate,
                                       LocalDate endDate, MarketAnalyzer analyzer) {
        System.out.println("\n====================================");
        System.out.println("Stock Analysis");
        System.out.println("====================================");
        System.out.println("Ticker: " + stock.getTicker());
        System.out.println("Period: " + startDate.format(DATE_FORMAT) + " - " + endDate.format(DATE_FORMAT));
        System.out.println("Records Found: " + analyzer.getRecords().size());
        System.out.printf("Highest Closing Price: $%.2f%n", analyzer.highestClose());
        System.out.printf("Lowest Closing Price: $%.2f%n", analyzer.lowestClose());
        System.out.printf("Average Closing Price: $%.2f%n", analyzer.averageClose());
        System.out.printf("Price Change: %+.2f%%%n", analyzer.priceChangePercent());
        System.out.printf("Average Trading Volume: %,.0f%n", analyzer.averageVolume());

        PriceRecord best = analyzer.bestTradingDay();
        PriceRecord worst = analyzer.worstTradingDay();

        if (best != null) {
            System.out.printf("Best Trading Day: %s (%+.2f%%)%n",
                    best.getDate().format(DATE_FORMAT), analyzer.getDailyChangePercent(best));
            System.out.printf("Worst Trading Day: %s (%+.2f%%)%n",
                    worst.getDate().format(DATE_FORMAT), analyzer.getDailyChangePercent(worst));
        } else {
            System.out.println("Best Trading Day: Not available (need at least two trading days)");
            System.out.println("Worst Trading Day: Not available (need at least two trading days)");
        }
    }
}
