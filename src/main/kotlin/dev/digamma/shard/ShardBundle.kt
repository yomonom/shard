package dev.digamma.shard

import com.intellij.DynamicBundle
import org.jetbrains.annotations.Nls
import org.jetbrains.annotations.PropertyKey

object ShardBundle {
    private const val BUNDLE = "messages.ShardBundle"

    private val instance = DynamicBundle(ShardBundle::class.java, BUNDLE)

    typealias Key = @PropertyKey(resourceBundle = BUNDLE) String

    fun message(key: Key, vararg params: Any): @Nls String = instance.getMessage(key, *params)
}
