package ru.otus.visitor;

import ru.otus.service.AtmService;
import ru.otus.storage.Storage;

public class TotalCollectingVisitor implements Visitor {
    @Override
    public int visit(AtmService atm) {
        final Storage storage = atm.getStorage();
        return storage.getAvailableBanknotes().stream()
                .map(banknote -> banknote.getAmount() * storage.getBanknotes(banknote))
                .reduce(Integer::sum)
                .orElse(0);
    }
}
