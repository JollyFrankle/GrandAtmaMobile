package com.example.grandatmahotel.utils.rv

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.grandatmahotel.R
import com.example.grandatmahotel.data.remote.model.JenisKamar
import com.example.grandatmahotel.data.remote.service.ApiConfig
import com.example.grandatmahotel.databinding.RvItemJenisKamarBinding

class JenisKamarRVAdapter(private val callback: OnItemCallback): RecyclerView.Adapter<JenisKamarRVAdapter.ViewHolder>() {

    private val list = mutableListOf<JenisKamar>()

    @SuppressLint("NotifyDataSetChanged")
    fun setList(list: List<JenisKamar>) {
        this.list.clear()
        this.list.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): JenisKamarRVAdapter.ViewHolder {
        val binding = RvItemJenisKamarBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: JenisKamarRVAdapter.ViewHolder, position: Int) {
        val jenisKamar = list[position]
        val context = holder.itemView.context
        val binding = holder.binding

        binding.tvNama.text = jenisKamar.nama
        binding.tvDescription.text = jenisKamar.shortDesc
        binding.tvUkuranKamar.text = context.getString(R.string.rvijk_ukuran, jenisKamar.ukuran)
        binding.tvKapasitas.text = context.getString(R.string.rvijk_kapasitas, jenisKamar.kapasitas)
        binding.tvRating.text = context.getString(R.string.rvijk_rating, jenisKamar.rating)

        Glide.with(context)
            .load(ApiConfig.getImage(jenisKamar.gambar))
            .placeholder(android.R.color.darker_gray)
            .into(binding.imgJenisKamar)

        binding.btnSelengkapnya.setOnClickListener {
            callback.onItemClicked(jenisKamar)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(val binding: RvItemJenisKamarBinding): RecyclerView.ViewHolder(binding.root)

    interface OnItemCallback {
        fun onItemClicked(data: JenisKamar)
    }
}