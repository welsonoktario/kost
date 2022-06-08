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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null

        tenantViewModel.setTotal(0)
        tenantViewModel.setAdditionals(arrayListOf())
        tenantViewModel.setServices(arrayListOf())
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
            val lamaMenyewa = tenant.lamaMenyewa()
            val sisaSewa = tenant.sisaSewa()
            val diffFromDue = tenant.diffFromDue()
            val telat = tenant.telat(Global.authKost.dendaBerlaku!!)

            binding.detailTenantNama.text = tenant.user.name
            binding.detailTenantPhone.text = tenant.user.phone
            binding.detailTenantTglMasuk.text = tenant.entryDate
            binding.detailTenantLama.text = "${tenant.lamaMenyewa()} Bulan"

            when {
                sisaSewa < 1 -> {
                    binding.detailTenantDue.text = "-"
                    binding.btnKonfirm.isEnabled = false
                    binding.btnTambah.isEnabled = false
                    binding.detailTenantDendaDurasi.visibility = View.GONE
                    binding.detailTenantDendaNominal.visibility = View.GONE
                    binding.detailTenantCardTagihan.visibility = View.GONE
                    binding.detailTenantDendaNull.visibility = View.VISIBLE
                }
                sisaSewa >= 1 -> {
                    binding.detailTenantDue.text = tenant.dueDate
                    binding.btnKonfirm.isEnabled = diffFromDue <= 15
                    binding.btnTambah.isEnabled = true
                    binding.detailTenantCardTagihan.visibility = View.VISIBLE
                }
                lamaMenyewa <= 1 -> {
                    binding.detailTenantDue.text = "-"
                    binding.btnKonfirm.isEnabled = false
                    binding.btnTambah.isEnabled = false
                    binding.detailTenantDendaDurasi.visibility = View.GONE
                    binding.detailTenantDendaNominal.visibility = View.GONE
                    binding.detailTenantDendaNull.visibility = View.VISIBLE
                }
                lamaMenyewa > 1 -> {
                    binding.detailTenantDue.text = tenant.dueDate
                    binding.btnKonfirm.isEnabled = diffFromDue <= 15
                    binding.btnTambah.isEnabled = true
                }
                diffFromDue <= 15 -> {
                    binding.btnKonfirm.isEnabled = true
                }
                diffFromDue > 15 -> {
                    binding.btnKonfirm.isEnabled = false
                }
            }

            if (sisaSewa >= 1 && telat >= 1) {
                binding.detailTenantDendaNull.visibility = View.GONE
                binding.detailTenantDendaDurasi.text =
                    "Telat membayar $telat hari"
                binding.detailTenantDendaNominal.text =
                    Global.authKost.nominalDenda?.let {
                        NumberUtil().rupiah(
                            tenant.nominalTelat(
                                Global.authKost
                            )
                        )
                    }
                binding.detailTenantDendaDurasi.visibility = View.VISIBLE
                binding.detailTenantDendaNominal.visibility = View.VISIBLE
            } else if (sisaSewa >= 1 && telat < 1) {
                binding.detailTenantDendaDurasi.visibility = View.GONE
                binding.detailTenantDendaNominal.visibility = View.GONE
                binding.detailTenantDendaNull.visibility = View.VISIBLE
            }

            refreshTotal()
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

                refreshTotal()
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

                refreshTotal()
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
        val lamaMenyewa = tenant.lamaMenyewa()
        val telat = tenant.telat(Global.authKost.dendaBerlaku!!)

        if (!this::dialogKonfirmasiBinding.isInitialized) {
            dialogKonfirmasiBinding =
                DialogKonfirmasiBinding.inflate(layoutInflater, binding.root, false)
        }

        dialogKonfirmasiBinding.dialogKonfirmasiTanggal.text = tenant.tanggalTagihan()
        dialogKonfirmasiBinding.dialogKonfirmasiJenis.text = roomType.name
        dialogKonfirmasiBinding.dialogKonfirmasiJenisHarga.text =
            NumberUtil().rupiah(roomType.cost!!)

        if (lamaMenyewa <= 1) {
            dialogKonfirmasiBinding.dialogKonfirmasiDendaNull.visibility = View.VISIBLE
            dialogKonfirmasiBinding.dialogKonfirmasiDendaDurasi.visibility = View.GONE
            dialogKonfirmasiBinding.dialogKonfirmasiDendaNominal.visibility = View.GONE
        } else if (lamaMenyewa > 1 && telat >= 1) {
            dialogKonfirmasiBinding.dialogKonfirmasiDendaDurasi.text =
                "Telat membayar $telat hari"
            dialogKonfirmasiBinding.dialogKonfirmasiDendaNominal.text =
                Global.authKost.nominalDenda?.let { NumberUtil().rupiah(tenant.nominalTelat(Global.authKost)) }

            dialogKonfirmasiBinding.dialogKonfirmasiDendaNull.visibility = View.GONE
            dialogKonfirmasiBinding.dialogKonfirmasiDendaDurasi.visibility = View.VISIBLE
            dialogKonfirmasiBinding.dialogKonfirmasiDendaNominal.visibility = View.VISIBLE
        } else {
            dialogKonfirmasiBinding.dialogKonfirmasiDendaNull.visibility = View.VISIBLE
            dialogKonfirmasiBinding.dialogKonfirmasiDendaDurasi.visibility = View.GONE
            dialogKonfirmasiBinding.dialogKonfirmasiDendaNominal.visibility = View.GONE
        }

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

        dialogKonfirmasiBinding.dialogKonfirmasiTotal.text =
            NumberUtil().rupiah(tenantViewModel.total.value!!)

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

                binding.detailTenantAdds.removeAllViews()
                binding.detailTenantAddsNull.visibility = View.VISIBLE
                binding.detailTenantService.removeAllViews()
                binding.detailTenantServiceNull.visibility = View.VISIBLE

                dialogKonfirmasi.dismiss()
                tenantViewModel.msg.value = res.getString("msg")
                refreshTotal()
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

    private fun refreshTotal() {
        val adds = tenantViewModel.additionals.value!!.sumOf { it.cost }
        val serv = tenantViewModel.services.value!!.sumOf { it.cost!! }
        var newTotal = tenantViewModel.roomType.value!!.cost!!

        if (tenant.sisaSewa() >= 1) {
            newTotal +=
                if (tenantViewModel.tenant.value!!.telat(Global.authKost.dendaBerlaku!!) > 1) {
                    val denda = tenantViewModel.tenant.value!!.nominalTelat(Global.authKost)
                    adds + serv + denda
                } else {
                    adds + serv
                }
        }

        tenantViewModel.refreshTotal()
        binding.detailTenantTotal.text = NumberUtil().rupiah(newTotal)
    }
}