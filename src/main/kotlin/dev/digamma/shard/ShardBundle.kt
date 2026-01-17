package dev.digamma.shard

import com.intellij.DynamicBundle
import org.jetbrains.annotations.Nls
import org.jetbrains.annotations.PropertyKey
import java.util.function.Supplier

object ShardBundle {
    const val BUNDLE = "messages.ShardBundle"
    private val INSTANCE = DynamicBundle(ShardBundle::class.java, BUNDLE)

    fun message(key: @PropertyKey(resourceBundle = BUNDLE) String, vararg params: Any): @Nls String =
        INSTANCE.getMessage(key, *params)

    fun lazyMessage(key: @PropertyKey(resourceBundle = BUNDLE) String, vararg params: Any): Supplier<@Nls String> =
        INSTANCE.getLazyMessage(key, *params)
}
