package com.example.grandatmahotel.utils.rv

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.grandatmahotel.R
import com.example.grandatmahotel.data.remote.model.Reservasi
import com.example.grandatmahotel.databinding.RvItemReservasiBinding
import com.example.grandatmahotel.utils.Utils

class HistoryReservasiRVAdapter(private val callback: OnItemCallback): RecyclerView.Adapter<HistoryReservasiRVAdapter.ViewHolder>() {

    private val list = mutableListOf<Reservasi>()

    @SuppressLint("NotifyDataSetChanged")
    fun setList(list: List<Reservasi>) {
        this.list.clear()
        this.list.addAll(list)
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
        val reservasi = list[position]
        val context = holder.itemView.context
        val binding = holder.binding

        val tanggalCheckIn = Utils.parseDate(reservasi.arrivalDate)
        val tanggalCheckOut = Utils.getTanggalCheckOut(tanggalCheckIn, reservasi.jumlahMalam)

        binding.tvBookingId.text = reservasi.idBooking ?: "(belum ada)"
        binding.tvTanggalCheckIn.text = Utils.formatDate(tanggalCheckIn, Utils.DF_DATE_READABLE)
        binding.tvTanggalCheckOut.text = Utils.formatDate(tanggalCheckOut, Utils.DF_DATE_READABLE)
        binding.tvDetailMenginap.text = context.getString(R.string.rvir_jumlah_tamu_format, reservasi.jumlahDewasa, reservasi.jumlahAnak, reservasi.jumlahMalam)
        binding.tvTotalHargaKamar.text = context.getString(R.string.rvir_total_harga_kamar, reservasi.total)

        val status = Utils.getRiwayatStatus(reservasi.status)
        binding.chipStatus.setChipBackgroundColorResource(status.first)
        binding.chipStatus.text = status.second
        binding.chipStatus.setTextColor(context.getColor(R.color.white))

        binding.btnViewDetail.setOnClickListener {
            callback.onItemClicked(reservasi)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(val binding: RvItemReservasiBinding): RecyclerView.ViewHolder(binding.root)

    interface OnItemCallback {
        fun onItemClicked(data: Reservasi)
    }
}