package com.nu9ve.quests.ui.contact

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import com.nu9ve.quests.R
import com.nu9ve.quests.databinding.FragmentContactListBinding
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_contact_list.*
import javax.inject.Inject

class ContactListFragment: DaggerFragment() {

    @Inject
    lateinit var mContactViewModel: ContactViewModel
    private lateinit var viewDataBinding: FragmentContactListBinding

    private lateinit var searchView: MenuItem


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        viewDataBinding = FragmentContactListBinding.inflate(inflater, null, false).apply {
            viewModel = mContactViewModel
            adapter = ContactListAdapter(mContactViewModel)
            lifecycleOwner = this@ContactListFragment
        }
        setHasOptionsMenu(true)
        return viewDataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        setObservers()
        setEvents()

        (activity as AppCompatActivity).supportActionBar?.show()

        if(viewDataBinding.viewModel?.contactList?.value.isNullOrEmpty()){
            viewDataBinding.viewModel?.getContacts()
        }
        viewDataBinding.viewModel?.setToolbarTitle("Contacts")
    }

    private fun setObservers(){
        mContactViewModel.run {
            contactList.observe(viewLifecycleOwner, Observer {
                viewDataBinding.adapter?.submitList(it)
            })

            refreshingList.observe(viewLifecycleOwner, Observer {
                swipeRefreshLayout.isRefreshing = it
            })

        }
    }

    private fun setEvents(){
        swipeRefreshLayout.setOnRefreshListener {
            searchView.collapseActionView()
            viewDataBinding.adapter?.clearFilter()
            viewDataBinding.viewModel?.resetList()
            viewDataBinding.viewModel?.getContacts()
        }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_searchbar, menu)

        searchView = menu.findItem(R.id.menu_nav_search_bar)
        val searchItem = searchView.actionView as SearchView

        searchItem.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(newText: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                viewDataBinding.adapter?.filter(newText)
                return true
            }

        })

    }


}