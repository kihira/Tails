package uk.kihira.tails.client;

import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

public final class LazyLoadAssetRegistry<K, V>
{
    private final HashMap<K, V> items = new HashMap<>();
    private final HashMap<K, CompletableFuture<V>> itemsLoading = new HashMap<>();
    private final Logger logger;
    private final V defaultValue;
    private final V errorValue;

    private final Function<K, CompletableFuture<V>> getter;

    /**
     * Creates an asset registry that will asynchronously load items when they are requested.
     * @param logger The logger that will be used to output information
     * @param getter The function that will be invoked when requesting an item
     * @param defaultValue The value to return whilst an item is loading
     * @param errorValue The value to store and return if an item fails to load
     */
    public LazyLoadAssetRegistry(Logger logger, Function<K, CompletableFuture<V>> getter, @Nullable V defaultValue, @Nullable V errorValue)
    {
        this.logger = logger;
        this.getter = getter;
        this.defaultValue = defaultValue;
        this.errorValue = errorValue;
    }

    public Optional<V> get(K key)
    {
        if (items.containsKey(key)) { return Optional.of(items.get(key)); }
        if (itemsLoading.containsKey(key))
        {
            CompletableFuture<V> future = itemsLoading.get(key);
            if (future.isDone())
            {
                V result;
                try
                {
                    result = future.get();
                }
                catch (InterruptedException | ExecutionException e)
                {
                    result = errorValue;
                    logger.error(String.format("Failed to load item %s", key.toString()), e);
                }
                items.put(key, result);
                itemsLoading.remove(key);

                return Optional.ofNullable(result);
            }

            return Optional.ofNullable(defaultValue);
        }

        // Start lazy loading the item
        CompletableFuture<V> future = getter.apply(key);
        if (future.isDone())
        {
            return safePut(key, future);
        }
        else
        {
            itemsLoading.put(key, future);
        }

        return Optional.empty();
    }

    /**
     * Removes a value from {@link #items} if it exists, and optionally from the cache as well.
     * If the item is still loading ({@link #itemsLoading}) then it cancels the operation.
     * @param key The key
     */
    public void remove(K key)
    {
        if (items.remove(key) != null) { return; }
        if (itemsLoading.containsKey(key))
        {
            CompletableFuture<V> future = itemsLoading.get(key);
            future.cancel(true);
            itemsLoading.remove(key);
        }
    }

    /**
     * Attempts to store the result of a CompletableFuture into {@link #items} and returns the result.
     * If an error occurs, returns an empty {@link Optional} and stores {@link #errorValue} instead.
     * @param key The key
     * @param future The {@link CompletableFuture} that contains the result
     */
    private Optional<V> safePut(K key, CompletableFuture<V> future)
    {
        V result;
        try
        {
            result = future.get();
        }
        catch (InterruptedException | ExecutionException e)
        {
            result = errorValue;
            logger.error(String.format("Failed to load item %s", key.toString()), e);
        }
        items.put(key, result);

        return Optional.ofNullable(result);
    }
}
