package com.example.purrfectfinder.Adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.purrfectfinder.R
import com.example.purrfectfinder.SerializableDataClasses.Filters

class FiltersAdapter(
    private var mFilters: List<String>
) : RecyclerView.Adapter<FiltersAdapter.ViewHolder>() {

    var allCheckBoxes: MutableList<CheckBox> = mutableListOf()

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView = itemView.findViewById<TextView>(R.id.tvBreedName)
        val checkBox = itemView.findViewById<CheckBox>(R.id.cbBreed)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val contactView = inflater.inflate(R.layout.filter_item, parent, false)

        return ViewHolder(contactView)
    }

    override fun getItemCount(): Int {
        return mFilters.size
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        var filter = mFilters[position]

        allCheckBoxes.add(viewHolder.checkBox)

        viewHolder.nameTextView.text = filter
    }

    fun getIfChecked(checkBox: CheckBox): Boolean {
        if (allCheckBoxes.contains(checkBox)) {
            return true
        }
        else return false

    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newFilters: List<String>) {
        mFilters = newFilters
        notifyDataSetChanged()
    }
}