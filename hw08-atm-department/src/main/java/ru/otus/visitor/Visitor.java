package ru.otus.visitor;

import ru.otus.service.AtmService;

/**
 * Visitor object.
 */
public interface Visitor {

    int visit(AtmService atm);

}
