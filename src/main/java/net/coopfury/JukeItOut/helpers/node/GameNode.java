package net.coopfury.JukeItOut.helpers.node;

import net.coopfury.JukeItOut.helpers.java.Procedure;

import java.util.HashSet;
import java.util.Set;

public class GameNode {
    boolean inTree;
    private final Set<GameNode> children = new HashSet<>();
    private final Set<Procedure> enterHandlers = new HashSet<>();
    private final Set<Procedure> exitHandlers = new HashSet<>();

    // Virtual methods
    protected void onEnterTree() { }

    protected void onExitTree() { }

    // Tree management
    public void addChild(GameNode node) {
        assert !node.inTree;
        assert !(node instanceof RootGameNode);

        children.add(node);
        if (inTree) {
            node.nodeEnter();
        }
    }

    public void removeChild(GameNode node) {
        assert node.inTree && children.contains(node);
        assert !(node instanceof RootGameNode);

        children.remove(node);
        if (inTree) {
            node.nodeExit();
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
    void nodeEnter() {
        assert !inTree;
        inTree = true;

        onEnterTree();
        for (Procedure handler : enterHandlers) {
            handler.run();
        }

        for (GameNode child : children) {
            child.nodeEnter();
        }
    }

    void nodeExit() {
        assert inTree;
        inTree = false;

        onExitTree();
        for (Procedure handler : exitHandlers) {
            handler.run();
        }

        for (GameNode child : children) {
            child.nodeExit();
        }
    }
}
