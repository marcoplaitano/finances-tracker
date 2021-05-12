package src.main.java.resources;

import java.util.Comparator;

public class Comparators {
    // sorting with this comparator results in having the item with
    // the MOST RECENT DATE at the top
    public Comparator<Transaction> compareByDescendingDate() {
        return new Comparator<Transaction>() {
            @Override
            public int compare(Transaction t0, Transaction t1) {
                return -t0.compareTo(t1);
            }
        };
    }

    // sorting with this comparator results in having the item with
    // the LEAST RECENT DATE at the top
    public Comparator<Transaction> compareByAscendingDate() {
        return new Comparator<Transaction>() {
            @Override
            public int compare(Transaction t0, Transaction t1) {
                return t0.compareTo(t1);
            }
        };
    }

    // sorting with this comparator results in having the item with
    // the HIGHEST AMOUNT at the top
    public Comparator<Transaction> compareByDescendingAmount() {
        return new Comparator<Transaction>() {
            @Override
            public int compare(Transaction t0, Transaction t1) {
                return -Double.compare(t0.getAmount(), t1.getAmount());
            }
        };
    }

    // sorting with this comparator results in having the item with
    // the LOWEST AMOUNT at the top
    public Comparator<Transaction> compareByAscendingAmount() {
        return new Comparator<Transaction>() {
            @Override
            public int compare(Transaction t0, Transaction t1) {
                return Double.compare(t0.getAmount(), t1.getAmount());
            }
        };
    }
}
