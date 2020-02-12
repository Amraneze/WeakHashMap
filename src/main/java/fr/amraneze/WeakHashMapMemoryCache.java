package fr.amraneze;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;

public class WeakHashMapMemoryCache<String> {

    // Time that that one value should last in seconds
    private long maxTime;
    private final WeakHashMap<String, Long> cacheMap;

    public WeakHashMapMemoryCache(long maxTime, final long timeInterval) {
        this.maxTime = maxTime * 1000;

        cacheMap = new WeakHashMap<>();

        if (maxTime > 0 && timeInterval > 0) {
            Thread thread = new Thread(() -> {
                while (true) {
                    try {
                        Thread.sleep(timeInterval * 1000);
                    } catch (InterruptedException ignored) {}
                    cleanup();
                }
            });
            thread.setDaemon(true);
            thread.start();
        }
    }

    public void put(String key) {
        synchronized (cacheMap) {
            cacheMap.put(key, System.currentTimeMillis());
        }
    }

    public boolean contains(String key) {
        synchronized (cacheMap) {
            return contains(key, false);
        }
    }

    public boolean contains(String key, boolean updateLastAccessedTime) {
        synchronized (cacheMap) {
            if (updateLastAccessedTime) {
                if (cacheMap.containsKey(key)) {
                    cacheMap.put(key, System.currentTimeMillis());
                    return true;
                }
                return false;
            }
            return cacheMap.containsKey(key);
        }
    }

    public void remove(String key) {
        synchronized (cacheMap) {
            cacheMap.remove(key);
        }
    }

    public int size() {
        synchronized (cacheMap) {
            return cacheMap.size();
        }
    }

    void cleanup() {
        var now = System.currentTimeMillis();
        ArrayList<String> deletedKeys;

        synchronized (cacheMap) {
            var entriesIterator = cacheMap.entrySet().iterator();
            deletedKeys = new ArrayList<>((cacheMap.size() / 2) + 1);

            entriesIterator.forEachRemaining((entry) -> {
                if (now > (maxTime + entry.getValue())) {
                    deletedKeys.add(entry.getKey());
                }
            });
        }

        deletedKeys.forEach((key) -> {
            synchronized (cacheMap) {
                cacheMap.remove(key);
            }
            Thread.yield();
        });
    }

}

