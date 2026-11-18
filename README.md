# Mini Market Terminal - Milestone 2
 
A Java console application that loads historical 2025 stock-market CSV files and provides a market information system: stock search, historical data, analysis, stock comparison, a personal watchlist, and sorting/filtering. It builds on the Historical Stock Data Analyzer from Milestone 1.
 
## Features
 
- Search/select a stock by ticker symbol
- Display historical stock records (sortable by date, closing price, or volume)
- Choose a start and end date
- Highest closing price
- Lowest closing price
- Average closing price
- Percentage price change over the selected period
- Average trading volume
- Best trading day by percentage increase
- Worst trading day by percentage decrease
- Watchlist: add stocks, remove stocks, and display all stocks in the watchlist with the latest available price and change (saved to `watchlist.txt`)
- Stock comparison: compare two or more stocks over a selected period (period return, average price, highest price, lowest price, average volume)
- Sorting and filtering: sort by ticker, price, percentage change, or trading volume; filter by minimum/maximum price and minimum average volume over a selected date range
- Text-based menu giving access to all major features
- Handles invalid ticker symbols
- Handles invalid dates and date ranges
- Handles missing data folders/files
## Classes
 
- `PriceRecord` - stores one day's market data
- `Stock` - stores a ticker symbol and its historical records
- `MarketDataManager` - reads the CSV files and organizes stocks
- `MarketAnalyzer` - performs calculations for a selected date range
- `Watchlist` - stores the user's watchlist, displays it, and saves it to a file
- `StockComparator` - compares the performance of two or more stocks over a period
- `MarketScreener` - sorts and filters market information for all stocks
- `Main` - provides the text-based user interface
## Data Format
 
Each CSV file is named after its ticker (for example `AAPL.csv`) and uses:
 
```
Date,Open,High,Low,Close,Volume
2025-01-02,136.0,138.88,134.63,138.31,198247166
```
 
The included dataset contains:
 
`AAPL`, `AMZN`, `AVGO`, `BRK`, `GOOGL`, `LLY`, `META`, `MSFT`, `NVDA`, `TSLA`
 
## Running in IntelliJ IDEA
 
1. Open the `StockMilestone2` folder as a project.
2. Make sure a Java JDK is configured for the project.
3. Open `src/Main.java`.
4. Run `Main.main()`.
5. Keep the `data` folder in the project root.
## Running from a Terminal
 
From the project folder:
 
```
javac -d out src/*.java
java -cp out Main
```
 
## Menu
 
```
================================================
MINI MARKET TERMINAL
================================================
1. Search Stock
2. View Historical Data
3. Analyze Stock
4. Compare Stocks
5. View Watchlist
6. Manage Watchlist
7. Sort & Filter Market
0. Exit
```
 
## Examples
 
Analyze Stock:
 
```
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
 
Watchlist:
 
```
MY WATCHLIST
Ticker   Latest Available Price     Change
--------------------------------------------------
AAPL     $XXX.XX                    +X.XX%
NVDA     $XXX.XX                    +X.XX%
MSFT     $XXX.XX                    -X.XX%
```
 
Compare Stocks:
 
```
                            AAPL            NVDA            MSFT
----------------------------------------------------------------
Period Return             +X.XX%          +X.XX%          -X.XX%
Average Price            $XXX.XX         $XXX.XX         $XXX.XX
Highest Price            $XXX.XX         $XXX.XX         $XXX.XX
Lowest Price             $XXX.XX         $XXX.XX         $XXX.XX
Average Volume        XX,XXX,XXX      XX,XXX,XXX      XX,XXX,XXX
```
 
## Notes
 
Daily percentage increase/decrease compares a trading day's closing price with the previous available trading day's close. The overall period price change compares the first closing price in the selected period with the last closing price in the selected period.
 
For the watchlist, the "latest price" is the most recent closing price in the local data, and the change compares it with the previous available closing price. All features use the local historical data only.
 
## Contributions
 
Milestone 1: Matt Tully - select or search for a stock using its ticker symbol; display historical records for the selected stock; specify a date range for the analysis; Matt Lawler - calculate the highest closing price during the selected period; calculate the lowest closing price during the selected period; calculate the average closing price; calculate the percentage price change over the selected period; James Walton - calculate the average trading volume; identify the trading day with the largest percentage increase; identify the trading day with the largest percentage decrease.
 
Milestone 2: (add who worked on the watchlist, stock comparison, sorting/filtering, and menu)
 
