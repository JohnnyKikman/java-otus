package ru.otus.service;

/**
 * Интерфейс для взаимодействия с департаментом АТМ.
 */
public interface AtmDepartmentService {

    /**
     * Сбор суммы остатков со всех АТМ.
     *
     * @return сумма остатков по всем АТМ
     */
    int getAtmAmounts();

    /**
     * Инициирует возврат состояния всех АТМ, принадлежащих этому департаменту, до начального.
     */
    void restoreInitialStates();

}
