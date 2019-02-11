package pogumedia.panasonic.sales.ui.base

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import pogumedia.panasonic.sales.model.ErrorData
import io.reactivex.disposables.CompositeDisposable

open class BaseViewModel: ViewModel() {

    val error: MutableLiveData<ErrorData?> = MutableLiveData()
    val success: MutableLiveData<Boolean?> = MutableLiveData()
    var compositeDisposable: CompositeDisposable = CompositeDisposable()

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

}