package pogumedia.panasonic.sales.service.api

import pogumedia.panasonic.sales.helper.util.Constants
import pogumedia.panasonic.sales.model.ErrorData
import io.reactivex.Observer
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class ApiObserver<T> constructor(private val compositeDisposable: CompositeDisposable) : Observer<T> {

    override fun onComplete() {

    }

    override fun onSubscribe(d: Disposable) {
        compositeDisposable.add(d)
    }

    override fun onNext(t: T) {
        onSuccess(t)
    }

    override fun onError(e: Throwable) {
        try{
            onError(ErrorData(throwable = e, message = e.localizedMessage))
        }catch (e : Exception){
            onError(ErrorData(throwable = e, message = "Your request cannot be processed because the network is unstable. Please try again"))
        }
    }

    abstract fun onSuccess(data: T)
    abstract fun onError(e: ErrorData)
}