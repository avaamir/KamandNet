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

fun <T> List<T>.diffSourceFromNewValues(
    newItems: List<T>,
    equalityCallback: EqualityCallback<T>,
    onSourceListChanged: OnSourceListChange<T>
): ArrayList<T> {
    val newList = ArrayList(newItems)
    val sourceList = ArrayList(this)
    val excludeList = ArrayList<T>()

    if (newList.isNotEmpty()) {
        for (index in sourceList.indices) {
            val oldItem = sourceList[index]
            for (i in newList.indices) {
                if (equalityCallback.areItemsSame(oldItem, newList[i])) { //items are same
                    val newItem = newList.removeAt(i)
                    if (!equalityCallback.areContentsSame(oldItem, newItem)) {
                        onSourceListChanged.onUpdateItem(oldItem, newItem)
                    }
                    break
                } else {
                    if (i == newList.size - 1) {
                        excludeList.add(oldItem)
                    }
                }
            }
        }
        if (excludeList.isNotEmpty()) {
            sourceList.removeAll(excludeList)
            onSourceListChanged.onRemoveItems(excludeList)
        }
        if (newList.isNotEmpty()) {
            sourceList.addAll(newList)
            onSourceListChanged.onAddItems(newList)
        }
    } else {
        onSourceListChanged.onRemoveItems(sourceList)
        sourceList.clear()
    }
    return sourceList.also { onSourceListChanged.onFinished(it) }
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

interface OnSourceListChange<T> {
    fun onAddItems(items: List<T>)
    fun onUpdateItem(oldItem: T, newItem: T)
    fun onRemoveItems(items: List<T>)
    fun onFinished(newList: ArrayList<T>)
}

interface EqualityCallback<T> {
    fun areItemsSame(oldItem: T, newItem: T): Boolean /** item ghabli ba jadid yeki hastand ya na? mamulan az tarigh id item moshakhas mishavad */
    fun areContentsSame(oldItem: T, newItem: T): Boolean /** 2 itemi ke ba ham yeksan budand aya mohtavaye yeksani ham darand ya na? agar nadarand pas niaz be update oldItem hast*/
}
