package ru.otus.service;

import ru.otus.service.internal.StorageState;
import ru.otus.visitor.TotalCollectingVisitor;

import java.util.Collection;

import static java.util.stream.Collectors.toList;

public class AtmDepartmentServiceImpl implements AtmDepartmentService {

    private final Collection<AtmService> atms;

    public AtmDepartmentServiceImpl(Collection<StorageState> initialStates) {
        atms = initialStates.stream().map(AtmServiceImpl::new).collect(toList());
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public int getAtmAmounts() {
        return atms.stream().map(atm -> atm.accept(new TotalCollectingVisitor())).reduce(Integer::sum).orElse(0);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void restoreInitialStates() {
        atms.forEach(AtmService::restoreInitialState);
    }
}
