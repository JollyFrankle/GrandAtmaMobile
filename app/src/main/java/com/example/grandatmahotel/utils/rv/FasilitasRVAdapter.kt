package com.example.grandatmahotel.utils.rv

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.grandatmahotel.R
import com.example.grandatmahotel.data.remote.model.FasilitasLayananTambahan
import com.example.grandatmahotel.data.remote.service.ApiConfig
import com.example.grandatmahotel.databinding.RvItemLayananBinding

class FasilitasRVAdapter(private val callback: OnItemCallback): RecyclerView.Adapter<FasilitasRVAdapter.ViewHolder>() {

    private val list = mutableListOf<FasilitasLayananTambahan>()
    private var maxKamar = 0
    private val fasilitasDipesans = mutableListOf<FasilitasDipesan>()

    @SuppressLint("NotifyDataSetChanged")
    fun setList(list: List<FasilitasLayananTambahan>) {
        this.list.clear()
        this.list.addAll(list)

        for (item in list) {
            // add to kamarDipesan
            val kamarD = fasilitasDipesans.find { it.fasilitas.id == item.id }
            if (kamarD == null) {
                fasilitasDipesans.add(FasilitasDipesan(0, item))
            } else {
                kamarD.fasilitas = item
            }
        }
        notifyDataSetChanged()

        // Supaya update harga di front end
        callback.onAmountChanged(fasilitasDipesans)
    }

    fun setMaxKamar(maxKamar: Int) {
        this.maxKamar = maxKamar
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FasilitasRVAdapter.ViewHolder {
        val binding = RvItemLayananBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FasilitasRVAdapter.ViewHolder, position: Int) {
        val item = list[position]
        val binding = holder.binding
        val context = binding.root.context
        val fasilitasD = fasilitasDipesans.find { it.fasilitas.id == item.id }!!

        // GAMBAR & IDENTITY
        Glide.with(context)
            .load(ApiConfig.getImage(item.gambar!!))
            .placeholder(android.R.color.darker_gray)
            .into(binding.imgFasilitas)
        binding.tvNamaFasilitas.text = item.nama
        binding.tvDescFasilitas.text = item.shortDesc
        binding.tvHargaFasilitas.text = context.getString(R.string.format_currency, item.tarif)
        binding.tvSatuanFasilitas.text = " per ${item.satuan}"

        binding.tvJumlahFasilitas.text = fasilitasD.count.toString()
        binding.btnAdd.setOnClickListener {
            if (
                fasilitasD.count < 5
            ) {
                fasilitasD.count += 1
                binding.tvJumlahFasilitas.text = fasilitasD.count.toString()
                callback.onAmountChanged(fasilitasDipesans)
            }
        }

        binding.btnSubtract.setOnClickListener {
            if (fasilitasD.count > 0) {
                fasilitasD.count -= 1
                binding.tvJumlahFasilitas.text = fasilitasD.count.toString()
                callback.onAmountChanged(fasilitasDipesans)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(val binding: RvItemLayananBinding): RecyclerView.ViewHolder(binding.root)

    interface OnItemCallback {
        fun onAmountChanged(listFD: List<FasilitasDipesan>)
    }

    data class FasilitasDipesan (
        var count: Int = 0,
        var fasilitas: FasilitasLayananTambahan
    )
}