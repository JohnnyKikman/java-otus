package ru.otus.storage.internal;

import ru.otus.exception.NoBanknoteException;
import ru.otus.value.Banknote;

import java.util.HashMap;
import java.util.Map;

public class Cell {

    private final Banknote banknote;
    private Cell nextCell;
    private int amount;

    public Cell(Banknote banknote, int initialAmount) {
        this.banknote = banknote;
        this.amount = initialAmount;
    }

    public static Cell empty() {
        return new Cell(Banknote.ONE_HUNDRED, 0);
    }

    public Cell and(Cell cell) {
        if (nextCell == null) {
            nextCell = cell;
        } else {
            nextCell.and(cell);
        }
        return this;
    }

    public void fetch(Banknote fetchedBanknote, int amount) {
        if (fetchedBanknote == banknote) {
            this.amount -= amount;
        } else if (nextCell != null) {
            nextCell.fetch(fetchedBanknote, amount);
        }
    }

    public void put(Banknote addedBanknote, int amount) {
        if (addedBanknote == banknote) {
            this.amount += amount;
        } else if (nextCell != null) {
            nextCell.put(addedBanknote, amount);
        }
    }

    public int get(Banknote fetchedBanknote) {
        if (fetchedBanknote == banknote) {
            return amount;
        } else if (nextCell != null) {
            return nextCell.get(fetchedBanknote);
        }
        throw new NoBanknoteException(fetchedBanknote);
    }

    public Map<Banknote, Integer> toMap() {
        final Map<Banknote, Integer> result = nextCell != null ? nextCell.toMap() : new HashMap<>();
        result.put(banknote, amount);
        return result;
    }

}
