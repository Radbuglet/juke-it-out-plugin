package net.coopfury.JukeItOut.helpers.spigot.command;

import java.util.Iterator;

public class ArgumentList {
    private static class ArgIterator implements Iterator<String> {
        private final ArgumentList list;
        private int index;
        private final int endIndex;

        ArgIterator(ArgumentList list, int startIndex, int endIndex) {
            this.list = list;
            index = startIndex;
            this.endIndex = endIndex;
        }

        @Override
        public boolean hasNext() {
            return index < endIndex;
        }

        @Override
        public String next() {
            index++;
            return list.args[index - 1];
        }
    }

    private String[] args;
    int rootOffset;

    public ArgumentList(String[] args) {
        this.args = args;
    }

    public Iterator<String> getArgs() {
        return new ArgIterator(this, rootOffset, args.length);
    }

    public Iterator<String> getLeftArgs() {
        return new ArgIterator(this, 0, rootOffset);
    }

    public String getArg(int index) {
        return args[index + rootOffset];
    }

    public int getArgCount() {
        return args.length - rootOffset;
    }
}