package com.example.grandatmahotel.utils.rv

import android.annotation.SuppressLint
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.grandatmahotel.R
import com.example.grandatmahotel.data.remote.model.TarifKamar
import com.example.grandatmahotel.data.remote.service.ApiConfig
import com.example.grandatmahotel.databinding.RvItemSearchBinding

class RoomSearchRVAdapter(private val callback: OnItemCallback): RecyclerView.Adapter<RoomSearchRVAdapter.ViewHolder>() {

    private val list = mutableListOf<TarifKamar>()
    private var jumlahKamarTerpilih = 0
    private var maxKamar = 0
    private val kamarDipesan = mutableListOf<KamarDipesan>()

    @SuppressLint("NotifyDataSetChanged")
    fun setList(list: List<TarifKamar>) {
        this.list.clear()
        this.list.addAll(list)

        for (item in list) {
            // add to kamarDipesan
            val kamarD = kamarDipesan.find { it.tarifKamar.jenisKamar.id == item.jenisKamar.id }
            if (kamarD == null) {
                kamarDipesan.add(KamarDipesan(0, item))
            } else {
                kamarD.tarifKamar = item
            }
        }
        notifyDataSetChanged()

        // Supaya update harga di front end
        callback.onKamarChanged(jumlahKamarTerpilih, kamarDipesan)
    }

    fun setMaxKamar(maxKamar: Int) {
        this.maxKamar = maxKamar
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RoomSearchRVAdapter.ViewHolder {
        val binding = RvItemSearchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RoomSearchRVAdapter.ViewHolder, position: Int) {
        val item = list[position]
        val binding = holder.binding
        val context = binding.root.context
        val kamarD = kamarDipesan.find { it.tarifKamar.jenisKamar.id == item.jenisKamar.id }!!

        // HEADER & UNGGULAN
        Glide.with(context)
            .load(ApiConfig.getImage(item.jenisKamar.gambar))
            .placeholder(android.R.color.darker_gray)
            .into(binding.imgJenisKamar)
        binding.tvNama.text = item.jenisKamar.nama
        binding.tvRating.text = context.getString(R.string.rvijk_rating, item.jenisKamar.rating)
        binding.tvKapasitas.text = context.getString(R.string.rvijk_kapasitas, item.jenisKamar.kapasitas)
        binding.tvUkuranKamar.text = context.getString(R.string.rvijk_ukuran, item.jenisKamar.ukuran)
        binding.tvBedType.text = item.jenisKamar.tipeBedAsReadable()

        // FASILITAS
        val fasilitas = item.jenisKamar.rincianAsList()
        val fasilitasLength = if(fasilitas.size > 6) 6 else fasilitas.size
        var fasilitasText = ""
        for (i in 0 until fasilitasLength) {
            fasilitasText += "${fasilitas[i]}\n"
        }
        fasilitasText = fasilitasText.trim()
        binding.tvFasilitas.text = fasilitasText

        // PRICE
        binding.tvNormalPrice.apply {
            if (item.rincianTarif.hargaDiskon == item.rincianTarif.harga) {
                visibility = android.view.View.GONE
            } else {
                visibility = android.view.View.VISIBLE
                paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                text = context.getString((R.string.format_currency), item.rincianTarif.harga)
            }
        }

        binding.tvDiscountPrice.apply {
            if (item.rincianTarif.hargaDiskon == item.rincianTarif.harga) {
                setTextColor(context.getColor(R.color.black))
            } else {
                setTextColor(context.getColor(R.color.green_500))
            }
            text = context.getString((R.string.format_currency), item.rincianTarif.hargaDiskon)
        }

        binding.tvJumlahKamar.text = kamarD.count.toString()
        binding.btnAddKamar.setOnClickListener {
            if (
                kamarD.count < item.rincianTarif.jumlahKamar &&
                jumlahKamarTerpilih < maxKamar
            ) {
                kamarD.count += 1
                jumlahKamarTerpilih += 1
                binding.tvJumlahKamar.text = kamarD.count.toString()
                callback.onKamarChanged(jumlahKamarTerpilih, kamarDipesan)
            }
        }

        binding.btnSubtractKamar.setOnClickListener {
            if (kamarD.count > 0) {
                kamarD.count -= 1
                jumlahKamarTerpilih -= 1
                binding.tvJumlahKamar.text = kamarD.count.toString()
                callback.onKamarChanged(jumlahKamarTerpilih, kamarDipesan)
            }
        }

        if (item.rincianTarif.jumlahKamar <= 0) {
            binding.root.apply {
                alpha = 0.5f
                isEnabled = false
            }
        } else {
            binding.root.apply {
                alpha = 1f
                isEnabled = true
            }
        }

        var catatan = ""
        if (item.rincianTarif.catatan.isNotEmpty()) {
            item.rincianTarif.catatan.forEach {
                catatan += "${it.message}\n"
            }
        } else {
            catatan = ""
        }
        binding.tvCatatan.text = catatan.trim()
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(val binding: RvItemSearchBinding): RecyclerView.ViewHolder(binding.root)

    interface OnItemCallback {
        fun onKamarChanged(jumlahKamar: Int, listKD: List<KamarDipesan>)
    }

    data class KamarDipesan (
        var count: Int = 0,
        var tarifKamar: TarifKamar
    )
}