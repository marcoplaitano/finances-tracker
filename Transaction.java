import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

public class Transaction implements Serializable, Comparable<Transaction> {
    private static final long serialVersionUID = 6025112299945533654L;
    private final double amount;
    private final LocalDate date;
    private String title;

    public Transaction(double amount, LocalDate date, String title) {
        this.amount = amount;
        this.date = date;
        this.title = title;
    }

    public double getAmount() {
        return amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Transaction other = (Transaction) obj;
        if (date.equals(other.getDate()))
            if (amount == other.getAmount())
                if (title.equalsIgnoreCase(other.getTitle()))
                    return true;
        return false;
    }

    @Override
    public int compareTo(Transaction other) {
        int diff = date.compareTo(other.getDate());
        if (diff == 0)
            return (int) (other.getAmount() - amount);
        return diff;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = hash * 7 + (int) amount * Objects.hashCode(date);
        return hash;
    }

    @Override
    public String toString() {
        String s = "";
        s += "Amount: " + amount + "\n";
        s += "Date:   " + date   + "\n";
        s += "Title:  " + title;
        return s;
    }
}
