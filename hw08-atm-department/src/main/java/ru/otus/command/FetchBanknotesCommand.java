package ru.otus.command;

import ru.otus.storage.Storage;
import ru.otus.value.Banknote;

public class FetchBanknotesCommand implements Command {

    private final Storage storage;
    private final Banknote banknote;
    private final int amount;

    public FetchBanknotesCommand(Storage storage, Banknote banknote, int amount) {
        this.storage = storage;
        this.banknote = banknote;
        this.amount = amount;
    }

    @Override
    public void execute() {
        storage.fetchBanknotes(banknote, amount);
    }
}
