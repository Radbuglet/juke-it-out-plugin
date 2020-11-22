package net.coopfury.JukeItOut.helpers.java.signal;

import net.coopfury.JukeItOut.helpers.java.Procedure;

public class ProcedureSignal extends BaseSignal<Procedure> {
    public void fire() {
        dispatch(Procedure::run);
    }
}
