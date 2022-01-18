package com.ubaya.kost.ui.owner.dashboard.room

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ubaya.kost.data.models.Tenant
import com.ubaya.kost.databinding.FragmentDetailTenantBinding

private const val ARG_TENANT = "tenant"

/**
 * A simple [Fragment] subclass.
 * Use the [DetailTenantFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DetailTenantFragment : Fragment() {
    private var _binding: FragmentDetailTenantBinding? = null
    private var tenant: Tenant? = null

    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            tenant = it.getParcelable(ARG_TENANT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailTenantBinding.inflate(inflater, container, false)
        return _binding!!.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param tenant Parameter 1.
         * @return A new instance of fragment DetailTenantFragment.
         */
        @JvmStatic
        fun newInstance(tenant: Tenant) =
            DetailTenantFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_TENANT, tenant)
                }
            }
    }
}