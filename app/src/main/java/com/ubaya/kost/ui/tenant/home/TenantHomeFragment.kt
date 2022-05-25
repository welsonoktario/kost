package com.ubaya.kost.ui.tenant.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import coil.load
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.badge.BadgeUtils
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.ubaya.kost.MainActivity
import com.ubaya.kost.R
import com.ubaya.kost.data.Global
import com.ubaya.kost.databinding.DialogFotoBinding
import com.ubaya.kost.databinding.DialogTenantPasswordBinding
import com.ubaya.kost.databinding.FragmentTenantHomeBinding
import com.ubaya.kost.ui.shared.notifications.NotificationsViewModel
import com.ubaya.kost.util.NumberUtil
import com.ubaya.kost.util.PrefManager
import com.ubaya.kost.util.VolleyClient
import com.ubaya.kost.util.observeOnce

class TenantHomeFragment : Fragment() {

    private var _binding: FragmentTenantHomeBinding? = null
    private var _dialogFotoBinding: DialogFotoBinding? = null
    private var _dialogPassBinding: DialogTenantPasswordBinding? = null

    private val binding get() = _binding!!
    private val dialogFotoBinding get() = _dialogFotoBinding!!
    private val dialogPassBinding get() = _dialogPassBinding!!

    private lateinit var dialogFoto: AlertDialog
    private lateinit var dialogPass: AlertDialog
    private lateinit var notifCountBadge: BadgeDrawable

    private val tenantViewModel by navGraphViewModels<TenantHomeViewModel>(R.id.mobile_navigation)
    private val notificationsViewModel by navGraphViewModels<NotificationsViewModel>(R.id.mobile_navigation)

    private val user = Global.authUser
    private val tenant = Global.authTenant

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTenantHomeBinding.inflate(inflater, container, false)
        _dialogFotoBinding = DialogFotoBinding.inflate(inflater, container, false)
        _dialogPassBinding = DialogTenantPasswordBinding.inflate(inflater, container, false)

        return _binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.tenant_home_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.menu_tenant_notifications -> findNavController().navigate(
                        R.id.action_fragment_tenant_home_to_fragment_tenant_notification
                    )
                    R.id.menu_tenant_message -> openChat()
                    R.id.menu_tenant_nota -> findNavController().navigate(
                        R.id.action_fragment_tenant_home_to_fragment_nota
                    )
                    R.id.menu_tenant_pass -> openPassword()
                    R.id.menu_tenant_logout -> logout()
                }

                return true
            }

        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        if (tenantViewModel.roomType.value == null) {
            tenantViewModel.loadDetailTenant(tenant.id)
        }

        if (notificationsViewModel.notifications.value!!.isEmpty()) {
            notificationsViewModel.loadNotifications()
        }
        tenantViewModel.msg.value = null

        initObserver()
        initView()
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun initView() {
        dialogFoto = MaterialAlertDialogBuilder(requireContext())
            .setView(dialogFotoBinding.root)
            .create()

        dialogPass = MaterialAlertDialogBuilder(requireContext())
            .setView(dialogPassBinding.root)
            .setTitle("Ubah Password")
            .setPositiveButton("Ubah", null)
            .setNegativeButton("Batal", null)
            .create()

        notifCountBadge = BadgeDrawable.create(requireContext())

        binding.homeTenantNama.text = user.name
        binding.homeTenantPhone.text = user.phone
        binding.homeTenantTglMasuk.text = tenant.entryDate
        binding.homeTenantDue.text = tenant.dueDate
        binding.homeTenantLama.text = "${tenant.lamaMenyewa()} Bulan"

        if (tenant.diffFromDue() > 7) {
            binding.tenantHomeCardTagihan.visibility = View.GONE
        } else {
            binding.tenantHomeCardTagihan.visibility = View.VISIBLE
        }

        binding.btnFoto.setOnClickListener {
            fotoIdentitas()
        }

        binding.btnHomeTenantService.setOnClickListener {
            findNavController().navigate(R.id.action_fragment_tenant_home_to_fragment_tenant_service)
        }

        binding.btnHomeTenantKomplain.setOnClickListener {
            findNavController().navigate(R.id.action_fragment_tenant_home_to_fragment_tenant_komplain)
        }

        notificationsViewModel.notifications.observe(viewLifecycleOwner) {
            val toolbar = (requireActivity() as MainActivity).findViewById<Toolbar>(R.id.toolbar)
            val count = it.filter { notif -> !notif.isRead }.size

            if (count > 0) {
                notifCountBadge.number = count
                BadgeUtils.attachBadgeDrawable(
                    notifCountBadge,
                    toolbar,
                    R.id.menu_tenant_notifications
                )
            } else {
                BadgeUtils.detachBadgeDrawable(
                    notifCountBadge,
                    toolbar,
                    R.id.menu_tenant_notifications
                )
            }
        }

        if (tenant.lamaMenyewa() <= 1) {
            binding.btnHomeTenantService.isEnabled = false
        }
    }

    private fun initObserver() {
        tenantViewModel.isLoading.observe(viewLifecycleOwner) {
            if (!it) {
                binding.homeTenantLoading.visibility = View.GONE
                binding.homeTenantLayout.visibility = View.VISIBLE
            } else {
                binding.homeTenantLoading.visibility = View.VISIBLE
                binding.homeTenantLayout.visibility = View.GONE
            }
        }

        tenantViewModel.roomType.observeOnce(viewLifecycleOwner) {
            binding.homeTenantTipeKamar.text = it.name
        }

        tenantViewModel.additionals.observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                binding.homeTenantAddsNull.visibility = View.VISIBLE
                binding.homeTenantAdds.removeAllViews()
            } else {
                binding.homeTenantAddsNull.visibility = View.GONE
                binding.homeTenantAdds.removeAllViews()

                it.forEach { add ->
                    val row = LinearLayout(requireContext())
                    row.layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    row.orientation = LinearLayout.HORIZONTAL

                    val name = TextView(requireContext())
                    name.text = add.description

                    val price = TextView(requireContext())
                    price.text = NumberUtil().rupiah(add.cost)

                    row.addView(
                        name,
                        LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f)
                    )
                    row.addView(price)

                    binding.homeTenantAdds.addView(row)
                }
            }
        }

        tenantViewModel.services.observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                binding.homeTenantServiceNull.visibility = View.VISIBLE
                binding.homeTenantService.removeAllViews()
            } else {
                binding.homeTenantServiceNull.visibility = View.GONE
                binding.homeTenantService.removeAllViews()

                it.forEach { service ->
                    val row = LinearLayout(requireContext())
                    row.layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    row.orientation = LinearLayout.HORIZONTAL

                    val name = TextView(requireContext())
                    name.text = service.name

                    val price = TextView(requireContext())
                    price.text = NumberUtil().rupiah(service.cost!!)

                    row.addView(
                        name,
                        LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f)
                    )
                    row.addView(price)

                    binding.homeTenantService.addView(row)
                }
            }
        }

        tenantViewModel.total.observe(viewLifecycleOwner) {
            binding.homeTenantTagihan.text = NumberUtil().rupiah(it)
        }

        tenantViewModel.msg.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) {
                Snackbar.make(binding.homeTenantLayoutMain, it, Snackbar.LENGTH_SHORT)
                    .setAction("OK") { }
                    .show()
            }
        }

        tenantViewModel.kost.observeOnce(viewLifecycleOwner) { kost ->
            val lamaMenyewa = tenant.lamaMenyewa()
            val telat = tenant.telat(kost.dendaBerlaku!!)

            if (lamaMenyewa <= 1) {
                binding.homeTenantDendaNull.visibility = View.VISIBLE
                binding.homeTenantDendaDurasi.visibility = View.GONE
                binding.homeTenantDendaNominal.visibility = View.GONE
            } else if (lamaMenyewa > 1 && telat >= 1) {
                binding.homeTenantDendaDurasi.text =
                    "Telat membayar $telat hari"
                binding.homeTenantDendaNominal.text =
                    kost.nominalDenda?.let {
                        NumberUtil().rupiah(tenant.nominalTelat(kost))
                    }

                binding.homeTenantDendaNull.visibility = View.GONE
                binding.homeTenantDendaDurasi.visibility = View.VISIBLE
                binding.homeTenantDendaNominal.visibility = View.VISIBLE
            } else if (lamaMenyewa > 1 && telat <= 1) {
                binding.homeTenantDendaNull.visibility = View.VISIBLE
                binding.homeTenantDendaDurasi.visibility = View.GONE
                binding.homeTenantDendaNominal.visibility = View.GONE
            }

            if (telat > 1) {
                tenantViewModel.setTotal(tenantViewModel.total.value!! + tenant.nominalTelat(kost))
            }
        }
    }

    private fun fotoIdentitas() {
        dialogFotoBinding.fotoKtp.load("${VolleyClient.BASE_URL}/storage/${tenant.ktp}").also {
            dialogFoto.show()
        }
    }

    private fun logout() {
        val prefs = PrefManager.getInstance(requireContext())
        prefs.clear()

        activity?.viewModelStore!!.clear()
        val navOptions = NavOptions
            .Builder()
            .setPopUpTo(R.id.fragment_tenant_home, true)
            .build()
        findNavController().navigate(R.id.fragment_login, null, navOptions)
        requireActivity().recreate()
    }

    private fun openChat() {
        val kost = Global.authKost
        val tenant = Global.authTenant
        val action =
            TenantHomeFragmentDirections.actionFragmentTenantHomeToFragmentTenantChatRoom(
                kost.id!!,
                tenant.id
            )
        findNavController().navigate(action)
    }

    private fun openPassword() {
        dialogPassBinding.dialogTenantPass.text!!.clear()
        dialogPassBinding.dialogTenantPassConfirm.text!!.clear()
        dialogPassBinding.dialogTenantPassConfirmLayout.isErrorEnabled = false

        dialogPass.show()

        dialogPass.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            dialogPassBinding.dialogTenantPassConfirmLayout.isErrorEnabled = false
            val pass = dialogPassBinding.dialogTenantPass.text.toString()
            val confirmPass = dialogPassBinding.dialogTenantPassConfirm.text.toString()

            if (pass != confirmPass) {
                dialogPassBinding.dialogTenantPassConfirmLayout.isErrorEnabled = true
                dialogPassBinding.dialogTenantPassConfirmLayout.error = "Password tidak cocok"
            } else {
                tenantViewModel.gantiPassword(tenant.id, pass)
                dialogPass.dismiss()
            }
        }
    }
}