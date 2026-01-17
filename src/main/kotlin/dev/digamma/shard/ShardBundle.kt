package dev.digamma.shard

import com.intellij.DynamicBundle
import org.jetbrains.annotations.Nls
import org.jetbrains.annotations.PropertyKey

object ShardBundle {
    const val BUNDLE = "messages.ShardBundle"
    private val INSTANCE = DynamicBundle(ShardBundle::class.java, BUNDLE)

    fun message(key: @PropertyKey(resourceBundle = BUNDLE) String, vararg params: Any): @Nls String =
        INSTANCE.getMessage(key, *params)
}
