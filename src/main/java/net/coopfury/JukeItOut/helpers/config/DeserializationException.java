package net.coopfury.JukeItOut.helpers.config;

public class DeserializationException extends Exception {
    public DeserializationException(String message) {
        super(message);
    }

    public static void asserted(boolean cond, String message) throws DeserializationException {
        if (!cond) throw new DeserializationException(message);
    }
}
