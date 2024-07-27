import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

// Class to represent a Stock
class Stock {
    private String symbol;
    private double price;

    public Stock(String symbol, double price) {
        this.symbol = symbol;
        this.price = price;
    }

    public String getSymbol() {
        return symbol;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return symbol + " ($" + price + ")";
    }
}

// Class to represent a Stock Holding
class StockHolding {
    private Stock stock;
    private int shares;
    private double purchasePrice;

    public StockHolding(Stock stock, int shares, double purchasePrice) {
        this.stock = stock;
        this.shares = shares;
        this.purchasePrice = purchasePrice;
    }

    public Stock getStock() {
        return stock;
    }

    public int getShares() {
        return shares;
    }

    public double getPurchasePrice() {
        return purchasePrice;
    }

    public void addShares(int shares) {
        this.shares += shares;
    }

    public double getTotalInvestment() {
        return shares * purchasePrice;
    }

    public double getCurrentValue() {
        return shares * stock.getPrice();
    }

    @Override
    public String toString() {
        return stock.getSymbol() + ": " + shares + " shares @ $" + purchasePrice + " each";
    }
}

// Class to represent a Transaction
class Transaction {
    private String type; // "Deposit", "Buy", or "Sell"
    private Stock stock;
    private int shares;
    private double price;
    private double totalCost;
    private double balanceAfterTransaction;

    public Transaction(String type, Stock stock, int shares, double price, double totalCost, double balanceAfterTransaction) {
        this.type = type;
        this.stock = stock;
        this.shares = shares;
        this.price = price;
        this.totalCost = totalCost;
        this.balanceAfterTransaction = balanceAfterTransaction;
    }

    @Override
    public String toString() {
        if ("Deposit".equals(type)) {
            return type + " $ " + totalCost + ". Balance After Transaction: $" + balanceAfterTransaction;
        } else {
            String stockSymbol = (stock != null) ? stock.getSymbol() : "Unknown";
            return type + " " + shares + " shares of " + stockSymbol + " at $" + price + " each. Total Cost: $" + totalCost + ". Balance After Transaction: $" + balanceAfterTransaction;
        }
    }
}

// Class to represent a Brokerage Account
class BrokerageAccount {
    private double balance;
    private Map<String, StockHolding> holdings;
    private Map<String, Stock> stocks;
    private List<Transaction> transactionHistory;

    public BrokerageAccount() {
        balance = 0.0;
        holdings = new HashMap<>();
        stocks = new HashMap<>();
        transactionHistory = new ArrayList<>();
    }

    public void addFunds(double amount) {
        balance += amount;
        transactionHistory.add(new Transaction("Deposit", null, 0, 0, amount, balance));
    }

    public void addStock(Stock stock) {
        stocks.put(stock.getSymbol(), stock);
    }

    public void displayAvailableStocks() {
        System.out.println("Available Stocks:");
        for (Stock stock : stocks.values()) {
            System.out.println(stock);
        }
    }

    public void displayHoldings() {
        double totalInvested = 0.0;
        double totalValue = 0.0;
        System.out.println("Current Holdings:");
        for (StockHolding holding : holdings.values()) {
            System.out.println(holding);
            totalInvested += holding.getTotalInvestment();
            totalValue += holding.getCurrentValue();
        }
        System.out.println("Total Invested: $" + totalInvested);
        System.out.println("Current Value: $" + totalValue);
        System.out.println("Net Profit/Loss: $" + (totalValue - totalInvested));
    }

    public void displayTransactionHistory() {
        System.out.println("Transaction History:");
        for (Transaction transaction : transactionHistory) {
            System.out.println(transaction);
            System.out.println("-------------------------"); // Separator for clarity
        }
    }

    public void buyStock(String symbol, int shares, double purchasePrice) {
        Stock stock = stocks.get(symbol);
        if (stock == null) {
            System.out.println("Stock not found.");
            return;
        }
        double totalCost = shares * purchasePrice;
        if (totalCost > balance) {
            System.out.println("Insufficient funds.");
            return;
        }
        balance -= totalCost;
        StockHolding holding = holdings.get(symbol);
        if (holding == null) {
            holdings.put(symbol, new StockHolding(stock, shares, purchasePrice));
        } else {
            holding.addShares(shares);
        }
        transactionHistory.add(new Transaction("Buy", stock, shares, purchasePrice, totalCost, balance));
    }

    public void sellStock(String symbol, int shares) {
        StockHolding holding = holdings.get(symbol);
        if (holding == null) {
            System.out.println("No holdings found for this stock.");
            return;
        }
        if (shares > holding.getShares()) {
            System.out.println("Not enough shares to sell.");
            return;
        }
        displayHoldings(); // Show holdings before selling
        double saleValue = shares * holding.getStock().getPrice();
        balance += saleValue;
        holding.addShares(-shares);
        if (holding.getShares() == 0) {
            holdings.remove(symbol);
        }
        transactionHistory.add(new Transaction("Sell", holding.getStock(), shares, holding.getStock().getPrice(), saleValue, balance));
    }

    public double getBalance() {
        return balance;
    }
}

// Main class with the command-line interface
public class BrokerageApp {
    public static void main(String[] args) {
        BrokerageAccount account = new BrokerageAccount();
        account.addStock(new Stock("AAPL", 150.0));
        account.addStock(new Stock("GOOGL", 2800.0));
        account.addStock(new Stock("AMZN", 3300.0));

        Scanner scanner = new Scanner(System.in);

        // Welcome Message
        System.out.println();
        System.out.println("-----------------------------------------------------------------------------");
        System.out.println("Welcome to the Brokerage Management System!");
        System.out.println("You can manage your funds, buy/sell stocks, and view your holdings and transaction history.");
        System.out.println("-----------------------------------------------------------------------------");

        while (true) {
            System.out.println("\nBrokerage Account Management:");
            System.out.println("1. Add Funds");
            System.out.println("2. Buy Stocks");
            System.out.println("3. Sell Stocks");
            System.out.println("4. Display Holdings");
            System.out.println("5. Display Transaction History");
            System.out.println("6. Exit");
            System.out.print("Choose an option: ");
            int option = scanner.nextInt();
            scanner.nextLine();  // Consume newline

            switch (option) {
                case 1:
                    System.out.print("Enter amount to add: ");
                    double amount = scanner.nextDouble();
                    scanner.nextLine();
                    account.addFunds(amount);
                    System.out.println("Added $" + amount + " to your account. Balance: $" + account.getBalance());
                    break;

                case 2:
                    account.displayAvailableStocks(); // Show available stocks
                    System.out.print("Enter stock symbol to buy: ");
                    String symbol = scanner.nextLine();
                    System.out.print("Enter number of shares: ");
                    int shares = scanner.nextInt();
                    System.out.print("Enter purchase price per share: ");
                    double price = scanner.nextDouble();
                    scanner.nextLine();
                    account.buyStock(symbol, shares, price);
                    System.out.println("Bought " + shares + " shares of " + symbol + " at $" + price + " each.");
                    break;

                case 3:
                    account.displayHoldings(); // Show holdings before selling
                    System.out.print("Enter stock symbol to sell: ");
                    String sellSymbol = scanner.nextLine();
                    System.out.print("Enter number of shares to sell: ");
                    int sellShares = scanner.nextInt();
                    scanner.nextLine();
                    account.sellStock(sellSymbol, sellShares);
                    System.out.println("Sold " + sellShares + " shares of " + sellSymbol + ".");
                    break;

                case 4:
                    account.displayHoldings();
                    break;

                case 5:
                    account.displayTransactionHistory();
                    break;

                case 6:
                    System.out.println("Exiting...");
                    scanner.close();
                    return;

                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }
}
