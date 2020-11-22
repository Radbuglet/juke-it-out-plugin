package net.coopfury.JukeItOut.helpers.node;

import net.coopfury.JukeItOut.helpers.java.Procedure;

import java.util.HashSet;
import java.util.Set;

public class GameNode {
    boolean inTree;
    private final Set<GameNode> children = new HashSet<>();
    private final Set<Procedure> enterHandlers = new HashSet<>();
    private final Set<Procedure> exitHandlers = new HashSet<>();

    // Tree management
    public void addChild(GameNode node) {
        assert !node.inTree;
        assert !(node instanceof RootGameNode);

        children.add(node);
        if (inTree) {
            node.onEnter();
        }
    }

    public void removeChild(GameNode node) {
        assert node.inTree && children.contains(node);
        assert !(node instanceof RootGameNode);

        children.remove(node);
        if (inTree) {
            node.onExit();
        }
    }

    public boolean isInTree() {
        return inTree;
    }

    // Entry/exit handling
    public Procedure bindEnterHandler(Procedure handler, boolean catchup) {
        enterHandlers.add(handler);

        if (inTree && catchup) {
            handler.run();
        }

        return handler;
    }

    public Procedure bindEnterHandler(Procedure handler) {
        enterHandlers.add(handler);
        return handler;
    }

    public void unbindEnterHandler(Procedure handler) {
        enterHandlers.remove(handler);
    }

    public Procedure bindExitHandler(Procedure handler, boolean catchup) {
        exitHandlers.add(handler);

        if (!inTree && catchup) {
            handler.run();
        }
        return handler;
    }

    public Procedure bindExitHandler(Procedure handler) {
        exitHandlers.add(handler);
        return handler;
    }

    public void unbindExitHandler(Procedure handler) {
        exitHandlers.remove(handler);
    }

    // Internal tree event handling
    void onEnter() {
        assert !inTree;
        inTree = true;

        for (Procedure handler : enterHandlers) {
            handler.run();
        }

        for (GameNode child : children) {
            child.onExit();
        }
    }

    void onExit() {
        assert inTree;
        inTree = false;

        for (Procedure handler : exitHandlers) {
            handler.run();
        }

        for (GameNode child : children) {
            child.onExit();
        }
    }
}
