package mp.amir.ir.kamandnet.utils.general

import kotlin.reflect.KProperty1


/*when you have to list, and one of them is sourceList and another is newValues for example came from server
* and you need to newValues set in the source list*/

fun <K, V, T> HashMap<K, V>.diffSourceFromNewValues(
    newValues: Collection<T>?,
    compareProperty: KProperty1<T, K>,
    onSourceMapChange: OnSourceMapChange<K, V, T>
) {
    val excludeList = ArrayList(this.keys)
    newValues?.forEach { item ->
        val keyId = compareProperty.get(item)
        val itemInSource = this[keyId]
        if (itemInSource != null) { // if Contains
            excludeList.remove(keyId)
            onSourceMapChange.onItemExistInBoth(keyId, itemInSource, item)
        } else {
            val value = onSourceMapChange.onAddItem(keyId, item)
            this[keyId] = value
        }
    }
    excludeList.forEach { key ->
        onSourceMapChange.onRemoveItem(key)
        this.remove(key)
    }
}


interface EqualityCallback<T> {
    fun areItemsSame(oldItem: T, newItem: T): Boolean
    fun areContentsSame(oldItem: T, newItem: T): Boolean
}


fun <T> List<T>.diffSourceFromNewValues(
    newItems: List<T>,
    equalityCallback: EqualityCallback<T>,
    onSourceListChanged: OnSourceListChange<T>
) : ArrayList<T> {
    val newList = ArrayList(newItems)
    val sourceList = ArrayList(this)
    sourceList.forEachIndexed { index, oldItem ->
        for (i in newList.indices) {
            if (equalityCallback.areItemsSame(oldItem, newList[i])) { //items are same
                val newItem = newList.removeAt(i)
                if (!equalityCallback.areContentsSame(oldItem, newItem)) {
                    onSourceListChanged.onUpdateItem(oldItem, newItem)
                }
                break
            } else {
                if (i == newList.size - 1) {
                    sourceList.removeAt(index)
                    onSourceListChanged.onRemoveItem(oldItem)
                }
            }
        }
    }
    sourceList.addAll(newList)
    onSourceListChanged.onAddItems(newItems)
    return sourceList
}


/*
fun <Key, Value> HashMap<Key, Value>.diffSourceFromNewValues(
    newValues: Collection<Key>, onSourceListChange: OnSourceMapChange<Key, Value>
) {
    val excludeList = ArrayList(this.keys)
    newValues.forEach { key ->
        if (excludeList.contains(key)) {
            excludeList.remove(key)
        } else {
            val value = onSourceListChange.onAddItem(key)
            this[key] = value
        }
    }
    excludeList.forEach { key ->
        onSourceListChange.onRemoveItem(key)
        this.remove(key)
    }
}
*/

interface OnSourceListChange<T> {
    fun onAddItems(item: List<T>)
    fun onUpdateItem(oldItem: T, newItem: T)
    fun onRemoveItem(item: T)
}

interface OnSourceMapChange<K, V, T> { //T is for new values list
    fun onAddItem(key: K, item: T): V
    fun onItemExistInBoth(
        keyId: K,
        value: V,
        item: T
    ) //Check equality if needed, if newValue are different then update the source item

    fun onRemoveItem(keyId: K)
}