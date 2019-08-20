package ru.otus.storage;

import ru.otus.command.Command;
import ru.otus.exception.NotEnoughBanknotesException;
import ru.otus.service.internal.StorageState;
import ru.otus.storage.internal.Cell;
import ru.otus.value.Banknote;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;

import static ru.otus.value.Banknote.FIFTY;
import static ru.otus.value.Banknote.FIVE_HUNDRED;
import static ru.otus.value.Banknote.FIVE_THOUSAND;
import static ru.otus.value.Banknote.ONE_HUNDRED;
import static ru.otus.value.Banknote.ONE_THOUSAND;
import static ru.otus.value.Banknote.TEN;
import static ru.otus.value.Banknote.TWO_HUNDRED;
import static ru.otus.value.Banknote.TWO_THOUSAND;

public class StorageImpl implements Storage {

    private Cell cells;
    private final Collection<Command> commands = new ArrayList<>();

    private static final int INITIAL_BANKNOTES = 1;
    private static final Cell DEFAULT_STATE;

    static {
        DEFAULT_STATE = new Cell(FIVE_THOUSAND, INITIAL_BANKNOTES)
                .and(new Cell(TWO_THOUSAND, INITIAL_BANKNOTES))
                .and(new Cell(ONE_THOUSAND, INITIAL_BANKNOTES))
                .and(new Cell(FIVE_HUNDRED, INITIAL_BANKNOTES))
                .and(new Cell(TWO_HUNDRED, INITIAL_BANKNOTES))
                .and(new Cell(ONE_HUNDRED, INITIAL_BANKNOTES))
                .and(new Cell(FIFTY, INITIAL_BANKNOTES))
                .and(new Cell(TEN, INITIAL_BANKNOTES));
    }

    public StorageImpl() {
        cells = DEFAULT_STATE;
    }

    public StorageImpl(StorageState state) {
        cells = cellsFrom(state);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public int getBanknotes(Banknote banknote) {
        return cells.get(banknote);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void fetchBanknotes(Banknote banknote, int amount) {
        final int currentAmount = cells.get(banknote);
        if (currentAmount - amount < 0) {
            throw new NotEnoughBanknotesException(banknote, amount, currentAmount);
        }
        cells.fetch(banknote, amount);
    }


    /**
     * {@inheritDoc}.
     */
    @Override
    public void putBanknotes(Banknote banknote, int amount) {
        cells.put(banknote, amount);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Collection<Banknote> getAvailableBanknotes() {
        return cells.toMap().keySet();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public StorageState getCurrentState() {
        return new StorageState(cells.toMap());
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void restore(StorageState state) {
        this.cells = cellsFrom(state);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void register(Command command) {
        commands.add(command);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void execute() {
        commands.forEach(Command::execute);
        commands.clear();
    }

    private Cell cellsFrom(StorageState state) {
        final Map<Banknote, Integer> amounts = state.getAmounts();
        if (amounts.isEmpty()) {
            return Cell.empty();
        }

        final Iterator<Banknote> banknotes = amounts.keySet()
                .stream()
                .sorted(Comparator.comparing(Banknote::getAmount).reversed())
                .collect(Collectors.toList()).iterator();
        final Banknote highestBanknote = banknotes.next();
        Cell cell = new Cell(highestBanknote, amounts.get(highestBanknote));
        while (banknotes.hasNext()) {
            final Banknote banknote = banknotes.next();
            cell = cell.and(new Cell(banknote, amounts.get(banknote)));
        }
        return cell;
    }
}
