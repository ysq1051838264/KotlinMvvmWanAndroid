package com.kotlin.mvvm.ui.project

import android.os.Bundle
import androidx.fragment.app.viewModels
import com.blankj.utilcode.util.StringUtils
import com.blankj.utilcode.util.ToastUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.kotlin.mvvm.R
import com.kotlin.mvvm.base.BaseFragment
import com.kotlin.mvvm.common.ScrollToTop
import com.kotlin.mvvm.common.handler_code_collect
import com.kotlin.mvvm.common.handler_code_un_collect
import com.kotlin.mvvm.databinding.FragmentPagerProjectBinding
import com.kotlin.mvvm.ext.setLinearLayoutManager

/**
 * description:
 *
 * @author Db_z
 * @Date 2021/12/24 11:07
 */
class ProjectViewPagerFragment : BaseFragment(), ScrollToTop {

    private val binding by lazy { FragmentPagerProjectBinding.inflate(layoutInflater) }
    private val mViewModel by viewModels<ProjectViewModel>()
    private val mAdapter by lazy { ProjectAdapter() }
    private var page = 1
    private var cid: Int? = 0
    private var position = 0

    companion object {
        fun newInstance(cid: Int) : ProjectViewPagerFragment {
            val fragment = ProjectViewPagerFragment()
            val bundle = Bundle().apply {
                putInt("cid", cid)
            }
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun getContentView() = binding.root

    override fun initView(bundle: Bundle?) {
        cid = bundle?.getInt("cid")
        setLoadSir(binding.refreshLayout)
        binding.recyclerView.setLinearLayoutManager(mAdapter)
        binding.refreshLayout.setEnableLoadMore(false)
        mAdapter.isAnimationFirstOnly = true
        mAdapter.setAnimationWithDefault(BaseQuickAdapter.AnimationType.ScaleIn)
        binding.refreshLayout.setOnRefreshListener {
            page = 1
            mViewModel.getProjectCidJson(page, cid)
        }
        mAdapter.loadMoreModule.setOnLoadMoreListener {
            page++
            mViewModel.getProjectCidJson(page, cid)
        }
        mAdapter.setCollectionListener { collect, id, position ->
            this.position = position
            if (collect) {
                mViewModel.unCollectList(id)
            } else {
                mViewModel.collect(id)
            }
        }
        mViewModel.mProjectPagerBean.observe(this) {
            if (it.curPage == 1) {
                if (it.datas.isEmpty()){
                    showEmpty()
                } else {
                    mAdapter.setList(it.datas)
                    showContent()
                }
                binding.refreshLayout.finishRefresh(true)
            } else {
                mAdapter.addData(it.datas)
                showContent()
            }
            mAdapter.loadMoreModule.loadMoreComplete()
            if (it.curPage == it.pageCount) {
                mAdapter.loadMoreModule.loadMoreEnd()
            }
        }
        mViewModel.handlerCode.observe(this) {
            when (it) {
                handler_code_collect -> {
                    mAdapter.data[position].collect = true
                    ToastUtils.showShort(StringUtils.getString(R.string.collect_success))
                }
                handler_code_un_collect -> {
                    mAdapter.data[position].collect = false
                    ToastUtils.showShort(StringUtils.getString(R.string.cancel_collect))
                }
            }
            mAdapter.notifyItemChanged(position)
        }
    }

    override fun initData() {
        page = 1
        mViewModel.getProjectCidJson(page, cid)
    }

    override fun scrollToTop() {
        binding.recyclerView.smoothScrollToPosition(0)
    }
}