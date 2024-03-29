package ru.javacat.ui

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import java.util.concurrent.atomic.AtomicBoolean

class SingleLiveEvent<T>: MutableLiveData<T>() {

    private var pending = AtomicBoolean(true)

    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        require(!hasActiveObservers()){
            error("Multiple observers registered but only one will be notified of changes.")
        }
        super.observe(owner){
            if (pending.get()) {
                pending.set(false)
                observer.onChanged(it)
            }
        }


    }

    override fun setValue(value: T) {
        pending.set(true)
        super.setValue(value)
    }
}