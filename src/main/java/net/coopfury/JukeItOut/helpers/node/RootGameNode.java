package net.coopfury.JukeItOut.helpers.node;

public class RootGameNode extends GameNode {
    public RootGameNode(boolean startInTree) {
        inTree = startInTree;
    }

    public void mount() {
        onEnter();
    }

    public void unmount() {
        onExit();
    }
}
