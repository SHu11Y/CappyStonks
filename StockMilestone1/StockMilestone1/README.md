# Historical Stock Data Analyzer - Milestone 1

A Java console application that loads historical 2025 stock-market CSV files and performs basic analysis for a ticker and date range selected by the user.

## Features

- Search/select a stock by ticker symbol
- Display historical stock records
- Choose a start and end date
- Highest closing price
- Lowest closing price
- Average closing price
- Percentage price change over the selected period
- Average trading volume
- Best trading day by percentage increase
- Worst trading day by percentage decrease
- Handles invalid ticker symbols
- Handles invalid dates and date ranges
- Handles missing data folders/files

## Classes

- `PriceRecord` - stores one day's market data
- `Stock` - stores a ticker symbol and its historical records
- `MarketDataManager` - reads the CSV files and organizes stocks
- `MarketAnalyzer` - performs calculations for a selected date range
- `Main` - provides the text-based user interface

## Data Format

Each CSV file uses:

```text
Date,Open,High,Low,Close,Volume
2025-01-02,136.0,138.88,134.63,138.31,198247166
```

The included dataset contains:

`AAPL`, `AMZN`, `AVGO`, `BRK`, `GOOGL`, `LLY`, `META`, `MSFT`, `NVDA`, `TSLA`

## Running in IntelliJ IDEA

1. Open the `StockMilestone1` folder as a project.
2. Make sure a Java JDK is configured for the project.
3. Open `src/Main.java`.
4. Run `Main.main()`.
5. Keep the `data` folder in the project root.

## Running from a Terminal

From the project folder:

```bash
javac -d out src/*.java
java -cp out Main
```

## Example

```text
Ticker: NVDA
Period: 01/01/2025 - 03/31/2025
Highest Closing Price: $XXX.XX
Lowest Closing Price: $XXX.XX
Average Closing Price: $XXX.XX
Price Change: +XX.XX%
Average Trading Volume: XX,XXX,XXX
Best Trading Day: XX/XX/2025 (+X.XX%)
Worst Trading Day: XX/XX/2025 (-X.XX%)
```

## Notes

Daily percentage increase/decrease compares a trading day's closing price with the previous available trading day's closing price. The overall period price change compares the first closing price in the selected period with the last closing price in the selected period.
