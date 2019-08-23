package ru.otus.command;

import ru.otus.storage.Storage;
import ru.otus.value.Banknote;

public class PutBanknotesCommand implements Command {

    private final Storage storage;
    private final Banknote banknote;
    private final int amount;

    public PutBanknotesCommand(Storage storage, Banknote banknote, int amount) {
        this.storage = storage;
        this.banknote = banknote;
        this.amount = amount;
    }

    @Override
    public void execute() {
        storage.putBanknotes(banknote, amount);
    }
}
