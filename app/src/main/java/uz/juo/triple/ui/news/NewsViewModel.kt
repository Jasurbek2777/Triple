package uz.juo.triple.ui.news

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import uz.juo.triple.paging.NewsSource

class NewsViewModel : ViewModel() {
    fun branches() =
        Pager(PagingConfig(30)) { NewsSource() }.flow.cachedIn(
            viewModelScope
        ).asLiveData()

}