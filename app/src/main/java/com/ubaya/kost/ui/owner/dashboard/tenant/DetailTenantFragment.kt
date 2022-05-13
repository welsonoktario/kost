package com.ubaya.kost.ui.owner.dashboard.tenant

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import coil.load
import com.android.volley.toolbox.JsonObjectRequest
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.ubaya.kost.R
import com.ubaya.kost.data.Global
import com.ubaya.kost.data.models.Additional
import com.ubaya.kost.data.models.Tenant
import com.ubaya.kost.databinding.*
import com.ubaya.kost.ui.owner.dashboard.DashboardViewModel
import com.ubaya.kost.util.NumberUtil
import com.ubaya.kost.util.VolleyClient
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class DetailTenantFragment : Fragment() {
    private var _binding: FragmentDetailTenantBinding? = null
    private lateinit var dialogKonfirmasiBinding: DialogKonfirmasiBinding
    private lateinit var dialogAddTagihanBinding: DialogAddTagihanBinding
    private lateinit var dialogPerpanjanganBinding: DialogPerpanjanganBinding
    private lateinit var dialogFotoBinding: DialogFotoBinding

    private lateinit var dialogFoto: AlertDialog
    private lateinit var dialogKonfirmasi: AlertDialog
    private lateinit var dialogAddTagihan: AlertDialog
    private lateinit var dialogPerpanjangan: AlertDialog
    private lateinit var dialogHapus: AlertDialog

    private lateinit var tenant: Tenant

    private val binding get() = _binding!!
    private val args: DetailTenantFragmentArgs by navArgs()
    private val tenantViewModel: TenantViewModel by navGraphViewModels(R.id.mobile_navigation)
    private val dashboardViewModel: DashboardViewModel by navGraphViewModels(R.id.mobile_navigation)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailTenantBinding.inflate(inflater, container, false)
        return _binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tenantViewModel.loadDetailTenant(args.room)
        tenantViewModel.msg.value = null

        initObserver()
        initView()
    }

    private fun initView() {
        binding.btnFoto.setOnClickListener {
            fotoIdentitas()
        }

        binding.btnKonfirm.setOnClickListener {
            btnKonfirm()
        }

        binding.btnTambah.setOnClickListener {
            btnTambah()
        }

        binding.btnPerpanjang.setOnClickListener {
            btnPerpanjang()
        }

        binding.btnHapus.setOnClickListener {
            btnHapus()
        }

        binding.btnPesan.setOnClickListener {
            sendMessage()
        }

        binding.btnBiodata.setOnClickListener {
            findNavController().navigate(R.id.action_fragment_detail_tenant_to_fragment_edit_tenant)
        }
    }

    private fun initObserver() {
        tenantViewModel.isLoading.observe(viewLifecycleOwner) {
            if (!it) {
                binding.detailTenantLoading.visibility = View.GONE
                binding.detailTenantLayout.visibility = View.VISIBLE
            } else {
                binding.detailTenantLoading.visibility = View.VISIBLE
                binding.detailTenantLayout.visibility = View.GONE
            }
        }

        tenantViewModel.roomType.observe(viewLifecycleOwner) {
            binding.detailTenantTipeKamar.text = it.name
        }

        tenantViewModel.tenant.observe(viewLifecycleOwner) {
            tenant = it!!

            binding.detailTenantNama.text = tenant.user.name
            binding.detailTenantPhone.text = tenant.user.phone
            binding.detailTenantTglMasuk.text = tenant.entryDate
            binding.detailTenantLama.text = "${tenant.lamaMenyewa()} Bulan"

            if (tenant.lamaMenyewa() <= 1) {
                binding.btnKonfirm.isEnabled = false
                binding.btnTambah.isEnabled = false
                binding.detailTenantCardTagihan.visibility = View.GONE
                binding.detailTenantDue.text = "-"
            } else {
                binding.detailTenantCardTagihan.visibility = View.VISIBLE
                binding.detailTenantDue.text = tenant.dueDate
            }

            if (tenant.diffFromDue() >= 15) {
                binding.btnKonfirm.isEnabled = false
            }
        }

        tenantViewModel.total.observe(viewLifecycleOwner) {
            binding.detailTenantTotal.text = NumberUtil().rupiah(it)
        }

        tenantViewModel.msg.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) {
                Snackbar.make(binding.detailTenantLayoutMain, it, Snackbar.LENGTH_SHORT)
                    .setAction("OK") { }
                    .show()
            }
        }

        tenantViewModel.additionals.observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                binding.detailTenantAddsNull.visibility = View.VISIBLE
                binding.detailTenantAdds.removeAllViews()
            } else {
                binding.detailTenantAddsNull.visibility = View.GONE
                binding.detailTenantAdds.removeAllViews()

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

                    binding.detailTenantAdds.addView(row)
                }
            }
        }

        tenantViewModel.services.observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                binding.detailTenantServiceNull.visibility = View.VISIBLE
                binding.detailTenantService.removeAllViews()
            } else {
                binding.detailTenantServiceNull.visibility = View.GONE
                binding.detailTenantService.removeAllViews()

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

                    binding.detailTenantService.addView(row)
                }
            }
        }
    }

    private fun fotoIdentitas() {
        if (!this::dialogFotoBinding.isInitialized) {
            dialogFotoBinding = DialogFotoBinding.inflate(layoutInflater, binding.root, false)
        }

        if (!this::dialogFoto.isInitialized) {
            dialogFotoBinding.fotoKtp.load("${VolleyClient.BASE_URL}/storage/${tenantViewModel.tenant.value!!.ktp}")
            dialogFoto = AlertDialog.Builder(requireContext())
                .setView(dialogFotoBinding.root)
                .create()
        }

        dialogFotoBinding.fotoKtp.load("${VolleyClient.BASE_URL}/storage/${tenantViewModel.tenant.value!!.ktp}")
        dialogFoto.show()
    }

    private fun btnKonfirm() {
        val tenant = tenantViewModel.tenant.value!!
        val roomType = tenantViewModel.roomType.value!!
        val services = tenantViewModel.services.value
        val adds = tenantViewModel.additionals.value
        var total = roomType.cost!!

        if (!this::dialogKonfirmasiBinding.isInitialized) {
            dialogKonfirmasiBinding =
                DialogKonfirmasiBinding.inflate(layoutInflater, binding.root, false)
        }

        dialogKonfirmasiBinding.dialogKonfirmasiTanggal.text = tenant.tanggalTagihan()
        dialogKonfirmasiBinding.dialogKonfirmasiJenis.text = roomType.name
        dialogKonfirmasiBinding.dialogKonfirmasiJenisHarga.text =
            NumberUtil().rupiah(roomType.cost!!)

        if (services.isNullOrEmpty()) {
            dialogKonfirmasiBinding.dialogKonfirmasiServiceNull.visibility = View.VISIBLE
            dialogKonfirmasiBinding.dialogKonfirmasiService.removeAllViews()
        } else {
            dialogKonfirmasiBinding.dialogKonfirmasiServiceNull.visibility = View.GONE
            dialogKonfirmasiBinding.dialogKonfirmasiService.removeAllViews()

            services.forEach {
                val row = LinearLayout(requireContext())
                row.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                row.orientation = LinearLayout.HORIZONTAL
                total += it.cost!!

                val name = TextView(requireContext())
                name.text = it.name

                val price = TextView(requireContext())
                price.text = NumberUtil().rupiah(it.cost!!)

                row.addView(
                    name,
                    LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f)
                )
                row.addView(price)

                dialogKonfirmasiBinding.dialogKonfirmasiService.addView(row)
            }
        }

        if (adds.isNullOrEmpty()) {
            dialogKonfirmasiBinding.dialogKonfirmasiAddsNull.visibility = View.VISIBLE
            dialogKonfirmasiBinding.dialogKonfirmasiAdds.removeAllViews()
        } else {
            dialogKonfirmasiBinding.dialogKonfirmasiAddsNull.visibility = View.GONE
            dialogKonfirmasiBinding.dialogKonfirmasiAdds.removeAllViews()
            adds.forEach {
                val row = LinearLayout(requireContext())
                row.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                row.orientation = LinearLayout.HORIZONTAL
                total += it.cost

                val name = TextView(requireContext())
                name.text = it.description

                val price = TextView(requireContext())
                price.text = NumberUtil().rupiah(it.cost)

                row.addView(
                    name,
                    LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f)
                )
                row.addView(price)

                dialogKonfirmasiBinding.dialogKonfirmasiAdds.addView(row)
            }
        }

        dialogKonfirmasiBinding.dialogKonfirmasiTotal.text = NumberUtil().rupiah(total)

        if (!this::dialogKonfirmasi.isInitialized) {
            dialogKonfirmasi = AlertDialog.Builder(requireContext())
                .setView(dialogKonfirmasiBinding.root)
                .setTitle("Konfirmasi Tagihan")
                .setPositiveButton("Konfirmasi", null)
                .setNegativeButton("Batal", null)
                .setCancelable(false)
                .create()

            dialogKonfirmasi.setOnShowListener {
                dialogKonfirmasi.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                    openDatePicker()
                }
            }
        }

        dialogKonfirmasi.show()
    }

    private fun openDatePicker() {
        val datePicker = MaterialDatePicker.Builder
            .datePicker()
            .setTitleText("Tanggal Konfirmasi")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()

        datePicker.show(childFragmentManager, "datePicker")

        datePicker.addOnPositiveButtonClickListener {
            val calendar: Calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Jakarta"))
            calendar.timeInMillis = it
            val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val formattedDate = format.format(calendar.time)

            konfirmasiPembayaran(formattedDate)
        }
    }

    private fun btnTambah() {
        if (!this::dialogAddTagihanBinding.isInitialized) {
            dialogAddTagihanBinding =
                DialogAddTagihanBinding.inflate(layoutInflater, binding.root, false)
        }

        if (!this::dialogAddTagihan.isInitialized) {
            dialogAddTagihan = AlertDialog.Builder(requireContext())
                .setView(dialogAddTagihanBinding.root)
                .setTitle("Tambah Tagihan")
                .setPositiveButton("Tambah") { _, _ -> addTagihan() }
                .setNegativeButton("Batal", null)
                .setCancelable(false)
                .create()

            dialogAddTagihan.setOnShowListener {
                dialogAddTagihan.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                    addTagihan()
                }
            }
        }

        dialogAddTagihan.show()
    }

    private fun btnPerpanjang() {
        if (!this::dialogPerpanjanganBinding.isInitialized) {
            dialogPerpanjanganBinding =
                DialogPerpanjanganBinding.inflate(layoutInflater, binding.root, false)
        }

        if (!this::dialogPerpanjangan.isInitialized) {
            dialogPerpanjangan = AlertDialog.Builder(requireContext())
                .setView(dialogPerpanjanganBinding.root)
                .setTitle("Perpanjang Masa Sewa")
                .setPositiveButton("Perpanjang") { _, _ -> perpanjang() }
                .setNegativeButton("Batal", null)
                .setCancelable(false)
                .create()
        }

        dialogPerpanjangan.show()
    }

    private fun btnHapus() {
        if (!this::dialogHapus.isInitialized) {
            dialogHapus = AlertDialog.Builder(
                requireContext(),
                R.style.ThemeOverlay_MaterialComponents_Dialog
            )
                .setTitle("Hapus Penyewa")
                .setMessage(
                    "Anda yakin ingin melakukan penghapusan penyewa dan jika penghapusan " +
                            "sudah terjadi maka tidak dapat dikembalikan. Penghapusan akan membuat " +
                            "kamar menjadi tersedia untuk disewakan. Apakah anda tetap ingin " +
                            "melakukan penghapusan?"
                )
                .setPositiveButton("Hapus") { _, _ -> hapus() }
                .setNegativeButton("Batal", null)
                .setCancelable(false)
                .create()
        }

        dialogHapus.show()
    }

    private fun konfirmasiPembayaran(date: String) {
        val total = tenantViewModel.total.value
        val url = VolleyClient.API_URL + "/tenants/${tenant.id}/konfirmasi?total=$total&date=$date"

        val request = object : JsonObjectRequest(url,
            { res ->
                val tenant = tenantViewModel.tenant.value!!
                tenant.dueDate = tenant.konfirmasi()
                tenantViewModel.setTenant(tenant)
                tenantViewModel.setServices(arrayListOf())
                tenantViewModel.setAdditionals(arrayListOf())
                dialogKonfirmasi.dismiss()
                tenantViewModel.msg.value = res.getString("msg")
            },
            { err ->
                Log.d("ERR", err.networkResponse.toString())
                dialogKonfirmasi.dismiss()

                try {
                    val data = JSONObject(String(err.networkResponse.data))
                    tenantViewModel.msg.value = data.getString("msg")
                } catch (e: Exception) {
                    Log.e("ERRR", e.message.toString())
                }
            }
        ) {
            override fun getHeaders() = hashMapOf(
                "Authorization" to "Bearer ${Global.authToken}"
            )
        }

        VolleyClient.getInstance(requireContext()).addToRequestQueue(request)
    }

    private fun addTagihan() {
        val nominal = dialogAddTagihanBinding.addTagihanNominal.text.toString().toInt()
        val deskripsi = dialogAddTagihanBinding.addTagihanDeskripsi.text.toString()

        val params = JSONObject()
        params.put("cost", nominal)
        params.put("description", deskripsi)

        val url = VolleyClient.API_URL + "/tenants/${tenant.id}/tagihan"

        val request = object : JsonObjectRequest(
            Method.POST, url, params,
            { res ->
                val data = res.getString("data")
                tenantViewModel.addAdditional(Gson().fromJson(data, Additional::class.java))
                tenantViewModel.setTotal(tenantViewModel.additionals.value!!.sumOf { add -> add.cost })
                dialogAddTagihan.dismiss()
                tenantViewModel.msg.value = res.getString("msg")
            },
            { err ->
                val data = JSONObject(String(err.networkResponse.data))
                dialogAddTagihan.dismiss()
                tenantViewModel.msg.value = data.getString("msg")
            }
        ) {
            override fun getHeaders() = hashMapOf(
                "Authorization" to "Bearer ${Global.authToken}"
            )
        }

        VolleyClient.getInstance(requireContext()).addToRequestQueue(request)
    }

    private fun perpanjang() {
        val durasi = dialogPerpanjanganBinding.perpanjanganDurasi.text.toString().toInt()
        val url = VolleyClient.API_URL + "/tenants/${tenant.id}/perpanjang?durasi=$durasi"

        val request = object : JsonObjectRequest(url,
            { res ->
                val tenant = tenantViewModel.tenant.value!!
                tenant.leaveDate = tenant.perpanjangan(durasi)
                tenantViewModel.setTenant(tenant)
                dialogPerpanjangan.dismiss()
                tenantViewModel.msg.value = res.getString("msg")
            },
            { err ->
                val data = JSONObject(String(err.networkResponse.data))
                dialogPerpanjangan.dismiss()
                tenantViewModel.msg.value = data.getString("msg")
            }
        ) {
            override fun getHeaders() = hashMapOf(
                "Authorization" to "Bearer ${Global.authToken}"
            )
        }

        VolleyClient.getInstance(requireContext()).addToRequestQueue(request)
    }

    private fun hapus() {
        val url = VolleyClient.API_URL + "/tenants/${tenant.id}"

        val request = object : JsonObjectRequest(Method.DELETE, url, null,
            { res ->
                dialogHapus.dismiss()
                tenantViewModel.msg.value = res.getString("msg")
                dashboardViewModel.rooms.value!!.apply {
                    val index = this.indexOfFirst { room -> room.tenant?.id == tenant.id }
                    this[index].tenant = null
                }
                findNavController().navigateUp()
            },
            { err ->
                Log.d("ERR", err.toString())
                dialogHapus.dismiss()
                try {
                    val data = JSONObject(String(err.networkResponse.data))
                    tenantViewModel.msg.value = data.getString("msg")
                } catch (e: Exception) {
                    tenantViewModel.msg.value = "Terjadi kesalahan server"
                }
            }
        ) {
            override fun getHeaders() = hashMapOf(
                "Authorization" to "Bearer ${Global.authToken}"
            )
        }

        VolleyClient.getInstance(requireContext()).addToRequestQueue(request)
    }

    private fun sendMessage() {
        val kost = Global.authKost
        val action =
            DetailTenantFragmentDirections.actionFragmentDetailTenantToFragmentChatRoom(
                kost.id!!,
                tenant.id
            )
        findNavController().navigate(action)
    }
}