package com.boats.n.objects;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.badlogic.gdx.math.Vector2;
import com.google.common.collect.EvictingQueue;

public class TouchTracker {

    private final Map<Integer, EvictingQueue<Vector2>> pointers;
    private final int historySize;

    public TouchTracker(int historySize)
    {
        this.historySize = historySize;
        pointers = new ConcurrentHashMap<Integer, EvictingQueue<Vector2>>();
    }

    public void pointerUp(Integer id)
    {
        pointers.remove(id);
    }

    public void pointerDown(Integer id, Vector2 position)
    {
        EvictingQueue<Vector2> queue = EvictingQueue.create(historySize);
        queue.add(position);
        pointers.put(id, queue);
    }

    public Vector2 pointerMove(Integer id, Vector2 position)
    {
        if (!pointers.containsKey(id))
        {
            pointerDown(id, position);
        }

        EvictingQueue<Vector2> queue = pointers.get(id);

        synchronized(queue)
        {
            queue.add(position);
            Vector2 heading = Vector2.Zero.cpy();
            Vector2 last = Vector2.Zero.cpy();
            for (Vector2 pos : queue)
            {
                heading = last.sub(pos);
                last = pos.cpy();
            }
            return heading;
        }
    }
}
