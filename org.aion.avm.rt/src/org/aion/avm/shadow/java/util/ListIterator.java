package org.aion.avm.shadow.java.util;

public interface ListIterator<E> extends Iterator<E>{

    boolean avm_hasNext();

    E avm_next();

    boolean avm_hasPrevious();

    E avm_previous();

    int avm_nextIndex();

    int avm_previousIndex();

    void avm_remove();

    void avm_set(E e);

    void avm_add(E e);

}
