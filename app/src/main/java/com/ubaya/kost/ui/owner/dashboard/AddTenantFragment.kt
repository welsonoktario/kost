package com.ubaya.kost.ui.owner.dashboard

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ubaya.kost.R
import com.ubaya.kost.data.models.Kost

private const val ARG_KOST = "KOST"

class AddTenantFragment : Fragment() {
    private var kost: Kost? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            kost = it.getParcelable(ARG_KOST)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_tenant, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance(kost: Kost) =
            AddTenantFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_KOST, kost)
                }
            }
    }
}