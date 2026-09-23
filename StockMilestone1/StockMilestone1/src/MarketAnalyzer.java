import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MarketAnalyzer {
    private final List<PriceRecord> allRecords;
    private final List<PriceRecord> records;

    public MarketAnalyzer(Stock stock, LocalDate startDate, LocalDate endDate) {
        if (stock == null) {
            throw new IllegalArgumentException("Stock cannot be null.");
        }
        if (startDate == null || endDate == null || startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date must be on or before end date.");
        }

        allRecords = new ArrayList<>(stock.getRecords());
        allRecords.sort(Comparator.comparing(PriceRecord::getDate));

        records = new ArrayList<>();
        for (PriceRecord record : allRecords) {
            if (!record.getDate().isBefore(startDate) && !record.getDate().isAfter(endDate)) {
                records.add(record);
            }
        }

        if (records.isEmpty()) {
            throw new IllegalArgumentException("No trading records were found in the selected date range.");
        }
    }

    public List<PriceRecord> getRecords() {
        return new ArrayList<>(records);
    }

    public double highestClose() {
        double highest = records.get(0).getClose();
        for (PriceRecord record : records) {
            if (record.getClose() > highest) {
                highest = record.getClose();
            }
        }
        return highest;
    }

    public double lowestClose() {
        double lowest = records.get(0).getClose();
        for (PriceRecord record : records) {
            if (record.getClose() < lowest) {
                lowest = record.getClose();
            }
        }
        return lowest;
    }

    public double averageClose() {
        double total = 0.0;
        for (PriceRecord record : records) {
            total += record.getClose();
        }
        return total / records.size();
    }

    public double priceChangePercent() {
        double firstClose = records.get(0).getClose();
        double lastClose = records.get(records.size() - 1).getClose();
        return ((lastClose - firstClose) / firstClose) * 100.0;
    }

    public double averageVolume() {
        long total = 0;
        for (PriceRecord record : records) {
            total += record.getVolume();
        }
        return (double) total / records.size();
    }

    public PriceRecord bestTradingDay() {
        PriceRecord bestRecord = null;
        double bestPercent = Double.NEGATIVE_INFINITY;

        for (PriceRecord record : records) {
            Double change = changeFromPreviousTradingDay(record);
            if (change != null && change > bestPercent) {
                bestPercent = change;
                bestRecord = record;
            }
        }
        return bestRecord;
    }

    public PriceRecord worstTradingDay() {
        PriceRecord worstRecord = null;
        double worstPercent = Double.POSITIVE_INFINITY;

        for (PriceRecord record : records) {
            Double change = changeFromPreviousTradingDay(record);
            if (change != null && change < worstPercent) {
                worstPercent = change;
                worstRecord = record;
            }
        }
        return worstRecord;
    }

    public double getDailyChangePercent(PriceRecord record) {
        Double change = changeFromPreviousTradingDay(record);
        return change == null ? 0.0 : change;
    }

    private Double changeFromPreviousTradingDay(PriceRecord current) {
        for (int i = 0; i < allRecords.size(); i++) {
            if (allRecords.get(i).getDate().equals(current.getDate())) {
                if (i == 0) {
                    return null;
                }
                double previousClose = allRecords.get(i - 1).getClose();
                return ((current.getClose() - previousClose) / previousClose) * 100.0;
            }
        }
        return null;
    }
}
