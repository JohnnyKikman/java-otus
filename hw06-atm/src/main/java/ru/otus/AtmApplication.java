package ru.otus;

import ru.otus.service.AtmServiceImpl;
import ru.otus.service.Storage;
import ru.otus.service.MaxToMinRetrievingStorage;
import ru.otus.value.Banknote;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class AtmApplication {

    public static void main(String[] args) {
        final Random random = new Random();
        final Storage storage = new MaxToMinRetrievingStorage();
        final AtmServiceImpl atm = new AtmServiceImpl(storage);

        // корректный кейс

        final Map<Integer, Integer> initialPack = new HashMap<>();
        Arrays.stream(Banknote.values()).forEach(faceValue -> initialPack.put(faceValue.getAmount(), random.nextInt(10)));
        atm.cashIn(initialPack);

        System.out.println(atm.getTotal());

        // снятие "нормального" объема средств

        System.out.println(atm.cashOut(1230));

        System.out.println(atm.getTotal());

        // попытка внести средства банкнотами неизвестных номиналов -> UnsupportedBanknotesException

        final Map<Integer, Integer> validAndInvalidPack = new HashMap<>();
        validAndInvalidPack.put(100, 1); // ok
        validAndInvalidPack.put(101, 1); // not ok
        atm.cashIn(validAndInvalidPack);

        // попытка снять средства, которые не выдать имеющимися банкнотами -> AmountNotFullyDisposableException

        atm.cashOut(123);

        // попытка снять средства в количестве, которого нет в банкомате -> InsufficientFundsException

        atm.cashOut(Integer.MAX_VALUE);
    }

}
