package org.aion.avm.internal;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;

import org.aion.avm.shadowapi.avm.InternalFunction;


public final class FunctionFactory {
    private final MethodHandles.Lookup lookup;
    private final MethodHandle target;

    public FunctionFactory(MethodHandles.Lookup lookup, MethodHandle target) {
        this.lookup = lookup;
        this.target = target;
    }

    public org.aion.avm.shadow.java.util.function.Function instantiate() {
        return InternalFunction.createFunction(this.lookup, this.target);
    }
}