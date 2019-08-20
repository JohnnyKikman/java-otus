package ru.otus.service;

import ru.otus.service.internal.AtmState;

import java.util.Collection;

import static java.util.stream.Collectors.toList;

public class AtmDepartmentServiceImpl implements AtmDepartmentService {

    private final Collection<AtmService> atms;

    public AtmDepartmentServiceImpl(Collection<AtmState> initialStates) {
        atms = initialStates.stream().map(AtmServiceImpl::new).collect(toList());
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public int getAtmAmounts() {
        return atms.stream().map(AtmService::getTotal).reduce(Integer::sum).orElse(0);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void restoreInitialStates() {
        atms.forEach(AtmService::restoreInitialState);
    }
}