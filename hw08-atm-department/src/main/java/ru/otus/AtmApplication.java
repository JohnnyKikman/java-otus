package ru.otus;

import ru.otus.service.AtmServiceImpl;
import ru.otus.value.Banknote;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class AtmApplication {

    public static void main(String[] args) {
        final Random random = new Random();
        final AtmServiceImpl atm = new AtmServiceImpl();

        // корректный кейс

        final Map<Banknote, Integer> initialPack = new HashMap<>();
        Arrays.stream(Banknote.values()).forEach(faceValue -> initialPack.put(faceValue, random.nextInt(10)));
        atm.cashIn(initialPack);

        System.out.println(atm.getTotal());

        // снятие "нормального" объема средств

        System.out.println(atm.cashOut(1230));

        System.out.println(atm.getTotal());

        // попытка снять средства, которые не выдать имеющимися банкнотами -> AmountNotFullyDisposableException

        atm.cashOut(123);

        // попытка снять средства в количестве, которого нет в банкомате -> InsufficientFundsException

        atm.cashOut(Integer.MAX_VALUE);
    }

}
