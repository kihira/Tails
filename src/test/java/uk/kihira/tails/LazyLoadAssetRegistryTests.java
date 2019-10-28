package uk.kihira.tails;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import uk.kihira.tails.client.LazyLoadAssetRegistry;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LazyLoadAssetRegistryTests
{
    @Test
    void Get_ErrorOccursDuringLoading_StoresAndReturnsErrorValue()
    {
        Logger logger = LogManager.getLogger();
        Function<String, CompletableFuture<String>> getter = (input) ->
        {
            CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> "VALID_VALUE");
            future.completeExceptionally(new ExecutionException(new RuntimeException()));
            return future;
        };
        LazyLoadAssetRegistry<String, String> registry = new LazyLoadAssetRegistry<>(logger, getter, "DEFAULT", "ERROR");

        registry.get("TEST");

        Optional<String> result = registry.get("TEST");
        assertTrue(result.isPresent());
        assertEquals("ERROR", result.get());
    }

    @Test
    void Get_FutureCompletesImmediately_StoresAndReturnsValue()
    {
        Logger logger = LogManager.getLogger();
        Function<String, CompletableFuture<String>> getter = (input) -> CompletableFuture.completedFuture("COMPLETED");
        LazyLoadAssetRegistry<String, String> registry = new LazyLoadAssetRegistry<>(logger, getter, "DEFAULT", "ERROR");

        Optional<String> result = registry.get("TEST");
        assertTrue(result.isPresent());
        assertEquals("COMPLETED", result.get());
    }

    @Test
    void Remove_FutureIsNotCompleted_CancelsFuture()
    {
        Logger logger = LogManager.getLogger();
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "";
        });
        Function<String, CompletableFuture<String>> getter = (input) -> future;
        LazyLoadAssetRegistry<String, String> registry = new LazyLoadAssetRegistry<>(logger, getter, "DEFAULT", "ERROR");

        registry.get("TEST");
        registry.remove("TEST");

        assertTrue(future.isCancelled());
    }
}
