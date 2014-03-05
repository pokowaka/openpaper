package org.openpaper.paint.action;

import java.util.LinkedList;

import org.openpaper.paint.BuildConfig;
import org.openpaper.paint.drawing.DrawingView;

/**
 * @author erwinj
 * 
 */
public class ActionQueue {

    private static final String TAG = "org.openpaper.paint.action.ActionQueue";

    private static final int SNAPSHOT_INTERVAL = 10;

    // This ALWAYS needs to be bigger than the snapshot_interval
    private static final int MAX_UNDO = 20 * SNAPSHOT_INTERVAL;

    private ActionQueueChangeListener listener;

    public interface ActionQueueChangeListener {

        void historyChanged(ActionQueue actionQueue, int undo, int redo);
    }

    // Important! We have the following invariant..
    // First element on the undo stack is always a snapshot action.
    LinkedList<PaintAction> undo = new LinkedList<PaintAction>();
    LinkedList<PaintAction> redo = new LinkedList<PaintAction>();
    DrawingView dv;

    public ActionQueue(DrawingView dv) {
        this.dv = dv;
        undo.add(new ClearScreenSnapshot(dv));
    }

    public void addAction(PaintAction pa) {
        // Take a snapshot every
        if (undo.size() % SNAPSHOT_INTERVAL == 0) {
            undo.push(new SnapshotAction(dv));
        }

        if (undo.size() > MAX_UNDO) {
            // remove from the beginning until we get a snapshot.
            undo.remove(0);
            while (!undo.get(0).isSnapshot()) {
                undo.remove(0);
            }

        }

        // Well, a new action is added on the undo stack.. Kill the redo stack!
        redo.clear();
        undo.push(pa);

        notifyListener();
    }

    public void redo(int steps) {
        while (steps > 0 && !redo.isEmpty()) {
            steps--;
            PaintAction pa = redo.pop();
            pa.execute(dv);
            undo.push(pa);
        }

        notifyListener();
    }

    public void redo() {
        redo(1);
    }

    public void undo() {
        undo(1);
    }

    public void undo(int steps) {
        int inv = Math.max(undo.size() - steps, 0);

        // Note, invariant says the first element is a snapshot!
        while (steps > 0 && undo.size() > 1) {
            steps--;
            redo.push(undo.pop());
        }

        // Now go back through the undo stack and find a snapshot action..
        // We start drawing again after a snapshot..
        int forward = 1;
        PaintAction action = undo.pop();
        redo.push(action);
        while (!action.isSnapshot()) {
            action = undo.pop();
            redo.push(action);
            forward++;
        }

        // We now have a snapshot action on the redo stack..
        // We push that back on the undo stack. This satisfies the invariant
        // That the first element of the stack always contains a snapshot
        // action.
        action = redo.pop();
        undo.push(action);
        action.execute(dv);
        forward--;

        for (; forward > 0; forward--) {
            action = redo.pop();
            action.execute(dv);
            undo.push(action);
        }

        if (BuildConfig.DEBUG && !(undo.size() == inv)) {
            throw new AssertionError();
        }

        notifyListener();
    }

    public int getUndoHistory() {
        return undo.size() - 1;
    }

    public int getRedoHistory() {
        return redo.size();
    }

    void notifyListener() {
        if (listener != null)
            listener.historyChanged(this, undo.size(), redo.size());
    }

    public void setActionQueueChangeListener(ActionQueueChangeListener listener) {
        this.listener = listener;
    }

    @Override
    public String toString() {
        return "ActionQueue [undo=" + undo.size() + ", redo=" + redo.size()
                + "]";
    }

    public void execute(PaintAction pa) {
        pa.execute(dv);
        addAction(pa);
    }

}
