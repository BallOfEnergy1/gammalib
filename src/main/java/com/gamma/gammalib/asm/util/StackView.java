package com.gamma.gammalib.asm.util;

import com.gamma.gammalib.core.GammaLibLogger;
import it.unimi.dsi.fastutil.Stack;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.Arrays;
import java.util.Objects;

public class StackView implements Cloneable {

    private int size;
    private final MethodInformation methodInfo;
    boolean underflow = false;
    private Stack<String> itemsOnStack;
    public Int2ObjectMap<String> localList = new Int2ObjectOpenHashMap<>();

    public StackView(MethodInformation methodInfo) {
        this.methodInfo = methodInfo;
        this.size = 0;
        this.itemsOnStack = new ObjectArrayList<>();
    }

    public int size() {
        return size;
    }

    public void clearStack() {
        while (!this.itemsOnStack.isEmpty()) {
            this.itemsOnStack.pop();
        }
        size = 0;
    }

    public void duplicateTop() {
        this.itemsOnStack.push(this.itemsOnStack.peek(0));
        size++;
    }

    public void duplicateTop(boolean top2) {
        if (top2) {
            this.itemsOnStack.push(this.itemsOnStack.peek(1));
            this.itemsOnStack.push(this.itemsOnStack.peek(0));
            size += 2;
        } else {
            duplicateTop();
        }
    }

    private void onStackUnderflow() {
        GammaLibLogger.debug(
            "[CHECK]: Error during ASM stack rebuilding: " + "Stack underflow during stack rebuild in "
                + methodInfo.methodNode().name
                + " in class "
                + methodInfo.classNode().name
                + ". Skipping.");
        underflow = true;
    }

    public String popFromStack() {
        if (size - 1 < 0) {
            onStackUnderflow();
            return "ERR";
        }
        size--;
        return this.itemsOnStack.pop();
    }

    public String[] popFromStack(int count) {
        String[] fromStack = new String[count];
        if (size - count < 0) {
            onStackUnderflow();
            Arrays.fill(fromStack, "ERR");
            return fromStack;
        }
        for (int i = 0; i < count; i++) {
            fromStack[i] = this.itemsOnStack.pop();
        }
        size -= count;
        return fromStack;
    }

    public String peekAtTop() {
        return this.itemsOnStack.peek(0);
    }

    public String peekAtSecond() {
        return this.itemsOnStack.peek(1);
    }

    public void pushToStack(String value) {
        this.itemsOnStack.push(value);
        size++;
    }

    public void pushToStack(String... values) {
        for (String value : values) {
            this.itemsOnStack.push(value);
        }
        size += values.length;
    }

    public Stack<String> getStack() {
        return itemsOnStack;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (StackView) obj;
        return this.size == that.size && Objects.equals(this.itemsOnStack, that.itemsOnStack);
    }

    @Override
    public String toString() {
        return "StackView[" + "size=" + size + ", " + "itemsOnStack=" + itemsOnStack + ']';
    }

    @Override
    public StackView clone() {
        try {
            StackView clone = (StackView) super.clone();
            // gruh
            clone.itemsOnStack = new ObjectArrayList<>((ObjectArrayList<String>) this.itemsOnStack);
            clone.localList = new Int2ObjectOpenHashMap<>(this.localList);
            if (this.size != clone.size) throw new IllegalStateException("Failed to clone stack view.");
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
