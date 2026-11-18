import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/** Reads the CSV files and organizes stocks. */
public class MarketDataManager {
    private final Map<String, Stock> stocks = new TreeMap<>();

    /** Loads every .csv file in the folder. Returns the number of stocks loaded. */
    public int loadData(String folderPath) {
        stocks.clear();
        File folder = new File(folderPath);
        File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".csv"));
        if (!folder.isDirectory() || files == null || files.length == 0) {
            return 0;
        }
        for (File file : files) {
            String name = file.getName();
            String ticker = name.substring(0, name.length() - 4).toUpperCase();
            Stock stock = new Stock(ticker);
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    PriceRecord record = parseLine(line);
                    if (record != null) stock.addRecord(record);
                }
            } catch (IOException e) {
                System.out.println("Could not read " + name + ": " + e.getMessage());
                continue;
            }
            if (!stock.getRecords().isEmpty()) {
                stock.sortRecords();
                stocks.put(ticker, stock);
            }
        }
        return stocks.size();
    }

    private PriceRecord parseLine(String line) {
        line = line.trim();
        if (line.isEmpty() || line.toLowerCase().startsWith("date")) return null;
        String[] p = line.split(",");
        if (p.length < 6) return null;
        try {
            return new PriceRecord(LocalDate.parse(p[0].trim()),
                    Double.parseDouble(p[1].trim()),
                    Double.parseDouble(p[2].trim()),
                    Double.parseDouble(p[3].trim()),
                    Double.parseDouble(p[4].trim()),
                    (long) Double.parseDouble(p[5].trim()));
        } catch (Exception e) {
            return null; // skip bad rows
        }
    }

    public Stock getStock(String ticker) {
        return ticker == null ? null : stocks.get(ticker.trim().toUpperCase());
    }

    public boolean hasStock(String ticker) { return getStock(ticker) != null; }

    public List<String> getTickers() { return new ArrayList<>(stocks.keySet()); }

    public List<Stock> getAllStocks() { return new ArrayList<>(stocks.values()); }
}
