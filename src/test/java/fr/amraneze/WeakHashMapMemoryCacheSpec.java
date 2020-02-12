package fr.amraneze;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.concurrent.TimeUnit;

class WeakHashMapMemoryCacheSpec {

    private final static long FIVE_MINUTES = 5 * 60;
    private final static long TEN_MINUTES = 2 * FIVE_MINUTES;

    @Test
    @DisplayName("It should add and remove some Strings")
    void itShouldAddAndRemove() {
        var weakHashMapMemoryCache = new WeakHashMapMemoryCache<>(TEN_MINUTES, FIVE_MINUTES);

        weakHashMapMemoryCache.put("SstpgUEGOwl0o80b");
        weakHashMapMemoryCache.put("jfr74M+VKsp6CCHaXac");
        weakHashMapMemoryCache.put("wPj++9OPLoa5Y");
        weakHashMapMemoryCache.put("YUYDsW9RBQivkghjkWcgFq4");
        weakHashMapMemoryCache.put("SstpgUEGOwl0oT5Fdxb7w");
        weakHashMapMemoryCache.put("9AM9YLZl6YyE=)l9DQ9vxmua0");
        Assertions.assertEquals(6, weakHashMapMemoryCache.size());

        weakHashMapMemoryCache.remove("SstpgUEGOwl0oT5Fdxb7w");
        Assertions.assertEquals(5, weakHashMapMemoryCache.size());

        weakHashMapMemoryCache.put("ffgpFT6TGHYQoYhgfh");
        weakHashMapMemoryCache.put("DH0o54GGTYPh5rMMbvaOljk");
        Assertions.assertEquals(7, weakHashMapMemoryCache.size());

    }

    @Test
    @DisplayName("It should expire the cached Strings")
    void itShouldExpireCachedStrings() throws InterruptedException {
        var weakHashMapMemoryCache = new WeakHashMapMemoryCache<>(3, 1);

        weakHashMapMemoryCache.put("SstpgUEGOwl0o800iuh4E");
        weakHashMapMemoryCache.put("jfr74MVKspny0TGT65T");
        TimeUnit.SECONDS.sleep(4);

        Assertions.assertEquals(0, weakHashMapMemoryCache.size());
    }

    @Test
    @DisplayName("It should clean the expired Strings")
    void itShouldCleanExpiredStrings() throws InterruptedException {
        var weakHashMapMemoryCache = new WeakHashMapMemoryCache<>(1, 1);
        for (int i = 0; i < 500000; i++) {
            weakHashMapMemoryCache.put(Integer.toString(i));
        }
        TimeUnit.SECONDS.sleep(1);

        var start = System.currentTimeMillis();
        weakHashMapMemoryCache.cleanup();
        var finish = (double) (System.currentTimeMillis() - start) / 1000.0;
        System.out.println("Finished after ".concat(String.valueOf(finish)).concat(" s"));
        Assertions.assertTrue(finish < 1);
        Assertions.assertEquals(0, weakHashMapMemoryCache.size());
    }

}