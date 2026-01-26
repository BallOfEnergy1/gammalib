package com.gamma.gammalib.asm;

import org.spongepowered.asm.lib.Type;

public class CommonNames {

    public static final String INIT = "<init>";

    public static final String ATOMIC_REF = "java/util/concurrent/atomic/AtomicReference";
    public static final String ATOMIC_BOOLEAN = "java/util/concurrent/atomic/AtomicBoolean";
    public static final String ATOMIC_INTEGER = "java/util/concurrent/atomic/AtomicInteger";
    public static final String ATOMIC_LONG = "java/util/concurrent/atomic/AtomicLong";

    @SuppressWarnings("unused")
    public static class DataTypes {

        public static final String BOOLEAN = Type.BOOLEAN_TYPE.getDescriptor();
        public static final String INTEGER = Type.INT_TYPE.getDescriptor();
        public static final String LONG = Type.LONG_TYPE.getDescriptor();
        public static final String FLOAT = Type.FLOAT_TYPE.getDescriptor();
        public static final String DOUBLE = Type.DOUBLE_TYPE.getDescriptor();
        public static final String CHAR = Type.CHAR_TYPE.getDescriptor();
        public static final String BYTE = Type.BYTE_TYPE.getDescriptor();
        public static final String SHORT = Type.SHORT_TYPE.getDescriptor();

        public static final String BOOLEAN_ARRAY = "[" + BOOLEAN;
        public static final String INTEGER_ARRAY = "[" + INTEGER;
        public static final String LONG_ARRAY = "[" + LONG;
        public static final String FLOAT_ARRAY = "[" + FLOAT;
        public static final String DOUBLE_ARRAY = "[" + DOUBLE;
        public static final String CHAR_ARRAY = "[" + CHAR;
        public static final String BYTE_ARRAY = "[" + BYTE;
        public static final String SHORT_ARRAY = "[" + SHORT;
    }
}
