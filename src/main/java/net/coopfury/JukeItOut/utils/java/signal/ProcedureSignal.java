package net.coopfury.JukeItOut.utils.java.signal;

import net.coopfury.JukeItOut.utils.java.Procedure;

public class ProcedureSignal extends BaseSignal<Procedure> {
    public void fire() {
        dispatch(Procedure::run);
    }
}
