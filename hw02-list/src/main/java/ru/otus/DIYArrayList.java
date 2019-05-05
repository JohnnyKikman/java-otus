package ru.otus;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

@SuppressWarnings("NullableProblems")
public class DIYArrayList<E> implements List<E> {

    private Object[] elements;
    private int size;

    // Constructors

    DIYArrayList() {
        elements = new Object[0];
    }

    DIYArrayList(int initialCapacity) {
        elements = new Object[initialCapacity];
    }

    // Required methods

    @Override
    public int size() {
        return size;
    }

    @Override
    public Iterator<E> iterator() {
        return new DIYIterator();
    }

    @Override
    public Object[] toArray() {
        return Arrays.copyOf(elements, size);
    }

    @Override
    public boolean add(E e) {
        if (size == elements.length) {
            elements = Arrays.copyOf(elements, size == 0 ? 1 : size * 2);
        }
        elements[size++] = e;
        return true;
    }

    @Override
    public ListIterator<E> listIterator() {
        return new DIYListIterator();
    }

    // Iterator classes

    private class DIYIterator implements Iterator<E> {
        int cursor;
        int lastReturned = -1;

        @Override
        public boolean hasNext() {
            return cursor != size;
        }

        @Override
        @SuppressWarnings("unchecked")
        public E next() {
            if (cursor >= size) {
                throw new NoSuchElementException();
            }
            lastReturned = cursor;
            return (E) elements[cursor++];
        }
    }

    private class DIYListIterator extends DIYIterator implements ListIterator<E> {
        // Required methods

        @Override
        public void set(E e) {
            if (lastReturned < 0 || lastReturned >= size) {
                throw new IndexOutOfBoundsException(lastReturned);
            }
            elements[lastReturned] = e;
        }

        // Not implemented methods

        @Override
        public boolean hasPrevious() {
            throw new UnsupportedOperationException();
        }

        @Override
        public E previous() {
            throw new UnsupportedOperationException();
        }

        @Override
        public int nextIndex() {
            throw new UnsupportedOperationException();
        }

        @Override
        public int previousIndex() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(E e) {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public String toString() {
        final StringBuilder result = new StringBuilder("[");
        for (int i = 0; i < size - 1; i++) {
            result.append(elements[i]).append(", ");
        }
        return result.append(elements[size - 1]).append(']').toString();
    }

    // Not implemented methods

    @Override
    public boolean isEmpty() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean contains(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T[] toArray(T[] ts) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(Collection<? extends E> collection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(int i, Collection<? extends E> collection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public E get(int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public E set(int i, E e) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(int i, E e) {
        throw new UnsupportedOperationException();
    }

    @Override
    public E remove(int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int indexOf(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int lastIndexOf(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ListIterator<E> listIterator(int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<E> subList(int i, int i1) {
        throw new UnsupportedOperationException();
    }
}
