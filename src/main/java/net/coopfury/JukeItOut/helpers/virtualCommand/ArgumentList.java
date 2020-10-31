package net.coopfury.JukeItOut.helpers.virtualCommand;

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

    public final String commandName;
    private final String[] args;
    public int rootOffset;

    public ArgumentList(String commandName, String[] args) {
        this.commandName = commandName;
        this.args = args;
    }

    public Iterable<String> iterateRight() {
        return () -> new ArgIterator(this, rootOffset, args.length);
    }

    public Iterable<String> iterateLeft() {
        return () -> new ArgIterator(this, 0, rootOffset);
    }

    public String getLeftStr(boolean fullCommand) {
        StringBuilder buffer = new StringBuilder();
        if (fullCommand) {
            buffer.append("/");
            buffer.append(commandName);
        }
        for (String part: iterateLeft()) {
            buffer.append(" ");
            buffer.append(part);
        }
        return buffer.toString();
    }

    public String getCommandPrefix() {
        return "/" + commandName;
    }

    public String getPart(int index) {
        return args[index + rootOffset];
    }

    public int getCount() {
        return args.length - rootOffset;
    }
}