package org.openpaper.paint.action;

import java.util.LinkedList;

import org.openpaper.paint.drawing.DrawingView;

/**
 * @author erwinj
 *
 */
public class ActionQueue {

    private static final String TAG = "org.openpaper.paint.action.ActionQueue";

    private static final int SNAPSHOT_INTERVAL = 100;

    private static final int MAX_UNDO = 20 * SNAPSHOT_INTERVAL;

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

        pa.execute(dv);
        undo.push(pa);
    }

    public void redo(int steps) {
        while (steps > 0 && !redo.isEmpty()) {
            steps--;
            PaintAction pa = redo.pop();
            pa.execute(dv);
            undo.push(pa);
        }
    }

    public void undo(int steps) {
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
            redo.pop().execute(dv);
        }
    }

    public int getUndoHistory() {
        return undo.size();
    }

    public int getRedoHistory() {
        return redo.size();
    }
}
