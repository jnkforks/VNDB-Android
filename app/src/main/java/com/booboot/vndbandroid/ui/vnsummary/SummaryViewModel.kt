package com.booboot.vndbandroid.ui.vnsummary

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.booboot.vndbandroid.App
import com.booboot.vndbandroid.extensions.format
import com.booboot.vndbandroid.model.vndb.VN
import com.booboot.vndbandroid.repository.VNRepository
import com.booboot.vndbandroid.ui.base.BaseViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class SummaryViewModel constructor(application: Application) : BaseViewModel(application) {
    @Inject lateinit var vnRepository: VNRepository
    val vnData: MutableLiveData<VN> = MutableLiveData()

    init {
        (application as App).appComponent.inject(this)
    }

    fun loadVn(vnId: Long, force: Boolean = true) {
        if (!force && vnData.value != null) return
        if (disposables.contains(DISPOSABLE_VN)) return

        disposables[DISPOSABLE_VN] = vnRepository.getItem(vnId)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { loadingData.value = true }
            .observeOn(Schedulers.computation())
            .map {
                it.aliases = it.aliases?.replace("\n", ", ")
                it.description = it.description?.format(getApplication<Application>().packageName) ?: ""
                it
            }
            .observeOn(AndroidSchedulers.mainThread())
            .doFinally {
                loadingData.value = false
                disposables.remove(DISPOSABLE_VN)
            }
            .subscribe({ vnData.value = it }, ::onError)
    }

    companion object {
        private const val DISPOSABLE_VN = "DISPOSABLE_VN"
    }
}