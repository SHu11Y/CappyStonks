import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.DirectoryStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MarketDataManager {
    private final Path dataDirectory;
    private final Map<String, Stock> stocks = new HashMap<>();

    public MarketDataManager(Path dataDirectory) {
        this.dataDirectory = dataDirectory;
    }

    public void loadAllStocks() throws IOException {
        if (!Files.exists(dataDirectory) || !Files.isDirectory(dataDirectory)) {
            throw new IOException("Data directory not found: " + dataDirectory.toAbsolutePath());
        }

        stocks.clear();
        try (DirectoryStream<Path> files = Files.newDirectoryStream(dataDirectory, "*_2025.csv")) {
            for (Path file : files) {
                String fileName = file.getFileName().toString();
                String ticker = fileName.substring(0, fileName.indexOf('_')).toUpperCase();
                stocks.put(ticker, new Stock(ticker, readFile(file)));
            }
        }

        if (stocks.isEmpty()) {
            throw new IOException("No stock CSV files were found in " + dataDirectory.toAbsolutePath());
        }
    }

    private List<PriceRecord> readFile(Path file) throws IOException {
        List<PriceRecord> records = new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(file)) {
            String line = reader.readLine(); // header
            if (line == null) {
                throw new IOException("File is empty: " + file.getFileName());
            }

            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",");
                if (parts.length != 6) {
                    throw new IOException("Invalid record in " + file.getFileName() + ": " + line);
                }

                try {
                    LocalDate date = LocalDate.parse(parts[0].trim());
                    double open = Double.parseDouble(parts[1].trim());
                    double high = Double.parseDouble(parts[2].trim());
                    double low = Double.parseDouble(parts[3].trim());
                    double close = Double.parseDouble(parts[4].trim());
                    long volume = Long.parseLong(parts[5].trim());
                    records.add(new PriceRecord(date, open, high, low, close, volume));
                } catch (RuntimeException e) {
                    throw new IOException("Invalid data in " + file.getFileName() + ": " + line, e);
                }
            }
        }

        return records;
    }

    public Stock getStock(String ticker) {
        if (ticker == null) return null;
        return stocks.get(ticker.trim().toUpperCase());
    }

    public boolean containsTicker(String ticker) {
        return getStock(ticker) != null;
    }

    public List<String> getAvailableTickers() {
        List<String> tickers = new ArrayList<>(stocks.keySet());
        tickers.sort(String::compareTo);
        return tickers;
    }
}
