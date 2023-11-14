package com.example.grandatmahotel.utils.rv

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.grandatmahotel.R
import com.example.grandatmahotel.data.remote.model.Reservasi
import com.example.grandatmahotel.databinding.RvItemReservasiBinding
import com.example.grandatmahotel.ui.customer.act_booking.BookingActivity
import com.example.grandatmahotel.utils.Utils

class HistoryReservasiRVAdapter(private val callback: OnItemCallback): RecyclerView.Adapter<HistoryReservasiRVAdapter.ViewHolder>() {

    private val list = mutableListOf<Reservasi>()
    private val filteredList = mutableListOf<Reservasi>()
    private var searchQuery = ""

    fun setList(list: List<Reservasi>) {
        this.list.clear()
        this.list.addAll(list)
        searchQuery = ""
        searchList()
    }

    fun searchList(query: String = searchQuery) {
        if (searchQuery.isNotBlank()) {
            val filteredList = mutableListOf<Reservasi>()
            for (reservasi in list) {
                if (reservasi.idBooking?.contains(query, true) == true) {
                    filteredList.add(reservasi)
                }
            }
            setFilteredList(filteredList)
        } else {
            setFilteredList(list)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setFilteredList(list: List<Reservasi>) {
        filteredList.clear()
        filteredList.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HistoryReservasiRVAdapter.ViewHolder {
        val binding = RvItemReservasiBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryReservasiRVAdapter.ViewHolder, position: Int) {
        val reservasi = filteredList[position]
        val context = holder.itemView.context
        val binding = holder.binding

        val tanggalCheckIn = Utils.parseDate(reservasi.arrivalDate)
        val tanggalCheckOut = Utils.parseDate(reservasi.departureDate)

        binding.tvBookingId.text = reservasi.idBooking ?: "(belum ada)"
        binding.tvTanggalCheckIn.text = Utils.formatDate(tanggalCheckIn, Utils.DF_DATE_READABLE)
        binding.tvTanggalCheckOut.text = Utils.formatDate(tanggalCheckOut, Utils.DF_DATE_READABLE)
        binding.tvDetailMenginap.text = context.getString(R.string.rvir_jumlah_tamu_format, reservasi.jumlahDewasa, reservasi.jumlahAnak, reservasi.jumlahMalam)
        binding.tvTotalHargaKamar.text = context.getString(R.string.rvir_total_harga_kamar, reservasi.total)

        val status = Utils.getRiwayatStatus(reservasi.status, reservasi.tanggalDlBooking)
        binding.chipStatus.setChipBackgroundColorResource(status.first)
        binding.chipStatus.text = status.second
        binding.chipStatus.setTextColor(context.getColor(R.color.white))

        binding.btnViewDetail.setOnClickListener {
            callback.onItemClicked(reservasi)
        }

        val cancelableStatus = Utils.isReservasiCancelable(reservasi)
        if (cancelableStatus == Utils.CancelableStatus.NO_REFUND || cancelableStatus == Utils.CancelableStatus.YES_REFUND || cancelableStatus == Utils.CancelableStatus.NO_CONSEQUENCE) {
            binding.btnCancel.visibility = ViewGroup.VISIBLE
        } else {
            binding.btnCancel.visibility = ViewGroup.GONE
        }
        binding.btnCancel.setOnClickListener {
            callback.onItemCancelled(reservasi)
        }

        // Kalau belum selesai booking, bisa lanjutkan
        if (status.second == "Belum Dibayar") {
            binding.btnContinue.visibility = ViewGroup.VISIBLE
        } else {
            binding.btnContinue.visibility = ViewGroup.GONE
        }

        binding.btnContinue.setOnClickListener {
            val intent = Intent(context, BookingActivity::class.java)
            intent.putExtra(BookingActivity.EXTRA_ID, reservasi.id.toLong())
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return filteredList.size
    }

    inner class ViewHolder(val binding: RvItemReservasiBinding): RecyclerView.ViewHolder(binding.root)

    interface OnItemCallback {
        fun onItemClicked(data: Reservasi)
        fun onItemCancelled(data: Reservasi)
    }
}