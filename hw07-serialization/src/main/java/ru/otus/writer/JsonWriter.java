package ru.otus.writer;

import java.io.Writer;

/**
 * Самодельный сервис сериализации POJO в JSON.
 */
public interface JsonWriter {

    /**
     * Сериализовать объект в строку формата JSON.
     *
     * @param object сериализуемый объект
     * @return строка, содержащая сериализованный объект
     */
    String toJson(Object object);

    /**
     * Сериализовать объект в строку формата JSON и записать его в переданный {@link Writer}.
     *
     * @param object сериализуемый объект
     * @param writer {@link Writer}, в который требуется записать JSON
     */
    void toJson(Object object, Writer writer);

}
